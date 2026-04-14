package top.xym.web.shop.conversation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.shop.conversation.mapper.ConversationSessionMapper;
import top.xym.web.shop.conversation.model.entity.ConversationSession;
import top.xym.web.shop.conversation.service.AdminConversationSessionService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

@Service
@AllArgsConstructor
public class AdminConversationSessionServiceImpl
        extends ServiceImpl<ConversationSessionMapper, ConversationSession>
        implements AdminConversationSessionService {

    private final SysUserService sysUserService;

    @Override
    public IPage<ConversationSession> pageList(Integer merchantId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ConversationSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConversationSession::getMerchantId, merchantId)
                .eq(ConversationSession::getMerchantDeleted, 0)
                .eq(ConversationSession::getDeleted, 0)
                .orderByDesc(ConversationSession::getLastMessageTime);

        Page<ConversationSession> page = new Page<>(pageNum, pageSize);

        IPage<ConversationSession> result = this.page(page, wrapper);

        // 给每个会话设置用户头像和手机号
        for (ConversationSession session : result.getRecords()) {
            SysUser user = sysUserService.getById(session.getUserId());
            if (user != null) {
                session.setAvatar(user.getAvatar());
                session.setPhone(user.getPhone());
            }
        }

        return result;
    }
}