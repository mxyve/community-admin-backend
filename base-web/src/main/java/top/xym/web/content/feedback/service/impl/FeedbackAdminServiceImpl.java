package top.xym.web.content.feedback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.web.content.feedback.entity.Feedback;
import top.xym.web.content.feedback.mapper.FeedbackMapper;
import top.xym.web.content.feedback.service.FeedbackAdminService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.mapper.SysUserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FeedbackAdminServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackAdminService {

    private final FeedbackMapper feedbackMapper;
    private final SysUserMapper userMapper;

    @Override
    public IPage<Feedback> pageList(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();

        // 按状态筛选
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }

        wrapper.eq(Feedback::getDeleted, 0)
                .orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        IPage<Feedback> feedbackPage  = this.page(page, wrapper);

        // 批量获取所有用户ID
        List<Long> userIds = feedbackPage.getRecords().stream()
                .map(Feedback::getUserId)
                .collect(Collectors.toList());

        if (!userIds.isEmpty()) {
            // 一次性批量查询用户（只查需要的字段）
            List<SysUser> users = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .in(SysUser::getUserId, userIds)
                            .select(SysUser::getUserId, SysUser::getUsername, SysUser::getNickName, SysUser::getPhone)
            );

            // 转成 Map<userId, User>
            Map<Long, SysUser> userMap = users.stream()
                    .collect(Collectors.toMap(
                            SysUser::getUserId,
                            u -> u
                    ));

            // 把用户名、手机号设置到 feedback
            for (Feedback fb : feedbackPage.getRecords()) {
                SysUser user = userMap.get(fb.getUserId());
                if (user != null) {
                    fb.setUsername(user.getUsername());
                    fb.setNickName(user.getNickName());
                    fb.setPhone(user.getPhone());
                }
            }
        }

        return feedbackPage;
    }

    @Override
    public void replyFeedback(Long id, String reply) {
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setReply(reply);
        // 改为已处理
        feedback.setStatus(1);
        feedback.setReplyTime(LocalDateTime.now());
        updateById(feedback);
    }
}