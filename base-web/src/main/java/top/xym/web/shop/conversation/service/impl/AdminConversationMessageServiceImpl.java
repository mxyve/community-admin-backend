package top.xym.web.shop.conversation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import top.xym.web.shop.conversation.config.AdminWebSocket;
import top.xym.web.shop.conversation.enums.MsgTypeEnum;
import top.xym.web.shop.conversation.enums.ReceiverTypeEnum;
import top.xym.web.shop.conversation.enums.SenderTypeEnum;
import top.xym.web.shop.conversation.mapper.ConversationMessageMapper;
import top.xym.web.shop.conversation.mapper.ConversationSessionMapper;
import top.xym.web.shop.conversation.model.dto.MessageSendRequest;
import top.xym.web.shop.conversation.model.entity.ConversationMessage;
import top.xym.web.shop.conversation.model.entity.ConversationSession;
import top.xym.web.shop.conversation.service.AdminConversationMessageService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AdminConversationMessageServiceImpl implements AdminConversationMessageService {

    private final ConversationMessageMapper messageMapper;
    private final ConversationSessionMapper sessionMapper;
    private final SysUserService sysUserService;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public List<ConversationMessage> getMessageList(Integer merchantId, Long sessionId) {
        // 权限校验
        ConversationSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getMerchantId().equals(Long.valueOf(merchantId))) {
            throw new RuntimeException("无权访问该会话");
        }

        LambdaQueryWrapper<ConversationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConversationMessage::getSessionId, sessionId)
                .eq(ConversationMessage::getDeleted, 0)
                .orderByAsc(ConversationMessage::getCreateTime);

        List<ConversationMessage> messageList = messageMapper.selectList(wrapper);

        // 获取用户信息，塞入头像、手机号
        SysUser user = sysUserService.getById(session.getUserId());
        if (user != null) {
            session.setAvatar(user.getAvatar());
            session.setPhone(user.getPhone());
        }

        return messageList;
    }

    @Transactional
    @Override
    public void sendMessage(Integer merchantId, MessageSendRequest request) {
        Long sessionId = request.getSessionId();
        ConversationSession session = sessionMapper.selectById(sessionId);

        // 权限校验
        if (session == null || !session.getMerchantId().equals(Long.valueOf(merchantId))) {
            throw new RuntimeException("无权操作");
        }

        // 保存消息
        ConversationMessage message = new ConversationMessage();
        message.setSessionId(sessionId);
        message.setSenderId(Long.valueOf(merchantId));
        message.setSenderType(SenderTypeEnum.MERCHANT);
        message.setReceiverId(session.getUserId());
        message.setReceiverType(ReceiverTypeEnum.USER);
        message.setMsgType(MsgTypeEnum.TEXT);
        message.setContent(request.getContent());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);

        messageMapper.insert(message);

        // 更新会话最后一条消息
        ConversationSession updateSession = new ConversationSession();
        updateSession.setId(sessionId);
        updateSession.setLastMessage(request.getContent());
        updateSession.setLastMessageTime(LocalDateTime.now());
        updateSession.setUpdateTime(LocalDateTime.now());
        sessionMapper.updateById(updateSession);

        // ==================== 推送自己（商家）聊天框实时显示 ====================
        Map<String, Object> pushMsg = new HashMap<>();
        pushMsg.put("id", message.getId());
        pushMsg.put("sessionId", message.getSessionId());
        pushMsg.put("senderId", message.getSenderId());
        pushMsg.put("senderType", message.getSenderType());
        pushMsg.put("receiverId", message.getReceiverId());
        pushMsg.put("receiverType", message.getReceiverType());
        pushMsg.put("content", message.getContent());
        pushMsg.put("createTime", message.getCreateTime());
        pushMsg.put("isRead", message.getIsRead());

        // 推送给商家自己
        AdminWebSocket.sendToMerchant(message.getSenderId(), JSON.toJSONString(pushMsg));

        // ================= 推送消息给用户端 =================
        try {
            String url = "http://localhost:8080/api/v1/chat/push-to-user";

            // 1. 设置请求头为 JSON，解决 415 错误
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. 封装参数（直接用你现有变量！）
            Map<String, Object> param = new HashMap<>();
            param.put("userId", session.getUserId());   // 接收方用户ID
            param.put("message", JSON.toJSONString(pushMsg)); // 传完整消息对象

            // 3. 封装请求（改名避免冲突！）
            HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(param, headers);
            log.info("【开始推送用户】url={}, userId={}, pushMsg={}", url, session.getUserId(), pushMsg);
            // 4. 发送请求
            restTemplate.postForObject(url, httpRequest, String.class);
            log.info("【推送用户成功】");
        } catch (Exception e) {
            log.error("通知用户端失败", e);
        }
    }

    @Transactional
    @Override
    public void markRead(Integer merchantId, Long sessionId) {
        ConversationSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getMerchantId().equals(Long.valueOf(merchantId))) {
            throw new RuntimeException("无权操作");
        }

        // 标记消息已读
        LambdaUpdateWrapper<ConversationMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConversationMessage::getSessionId, sessionId)
                .eq(ConversationMessage::getReceiverType, ReceiverTypeEnum.MERCHANT)
                .eq(ConversationMessage::getIsRead, 0);

        ConversationMessage update = new ConversationMessage();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        messageMapper.update(update, wrapper);

        // 清空未读数
        ConversationSession reset = new ConversationSession();
        reset.setId(sessionId);
        reset.setMerchantUnreadCount(0);
        sessionMapper.updateById(reset);
    }
}