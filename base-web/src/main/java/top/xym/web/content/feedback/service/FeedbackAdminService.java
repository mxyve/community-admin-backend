package top.xym.web.content.feedback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.xym.web.content.feedback.entity.Feedback;

public interface FeedbackAdminService {

    /**
     * 后台分页查询反馈列表（可按状态筛选）
     */
    IPage<Feedback> pageList(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 管理员回复反馈
     */
    void replyFeedback(Long id, String reply);
}
