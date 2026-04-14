package top.xym.web.shop.conversation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.utils.SecurityUtils;
import top.xym.web.shop.conversation.model.dto.MessageSendRequest;
import top.xym.web.shop.conversation.model.entity.ConversationSession;
import top.xym.web.shop.conversation.service.AdminConversationMessageService;
import top.xym.web.shop.conversation.service.AdminConversationSessionService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

@RestController
@RequestMapping("/api/admin/conversation")
@AllArgsConstructor
@Tag(name = "租户端-客服消息管理")
public class AdminConversationController {

    private final AdminConversationSessionService sessionService;
    private final AdminConversationMessageService messageService;
    private final SysUserService sysUserService;

    /**
     * 统一获取当前商家ID
     */
    private Integer getCurrentMerchantId() {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || user.getTenantId() == null) {
            throw new RuntimeException("当前用户不是租户管理员");
        }
        return user.getTenantId().intValue();
    }

    // ==================== 会话列表（左侧聊天列表） ====================
    @GetMapping("/session/page")
    @Operation(summary = "租户端-会话分页列表")
    public ResultVo<?> sessionPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        IPage<ConversationSession> page = sessionService.pageList(getCurrentMerchantId(), pageNum, pageSize);
        return ResultUtils.success("查询成功", page);
    }

    // ==================== 聊天记录（右侧窗口） ====================
    @GetMapping("/message/list")
    @Operation(summary = "获取会话消息记录")
    public ResultVo<?> messageList(@RequestParam Long sessionId) {
        return ResultUtils.success("查询成功",
                messageService.getMessageList(getCurrentMerchantId(), sessionId));
    }

    // ==================== 商家发送消息 ====================
    @PostMapping("/message/send")
    @Operation(summary = "商家发送消息")
    public ResultVo<?> send(@RequestBody MessageSendRequest request) {
        messageService.sendMessage(getCurrentMerchantId(), request);
        return ResultUtils.success("发送成功");
    }

    // ==================== 标记已读 ====================
    @PutMapping("/message/read")
    @Operation(summary = "标记会话消息已读")
    public ResultVo<?> read(@RequestParam Long sessionId) {
        messageService.markRead(getCurrentMerchantId(), sessionId);
        return ResultUtils.success("操作成功");
    }
}