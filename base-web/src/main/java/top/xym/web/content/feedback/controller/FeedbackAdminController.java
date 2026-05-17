package top.xym.web.content.feedback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.content.feedback.entity.Feedback;
import top.xym.web.content.feedback.service.FeedbackAdminService;

@RestController
@RequestMapping("/api/admin/feedback")
@AllArgsConstructor
@Tag(name = "后台-意见反馈管理")
public class FeedbackAdminController {

    private final FeedbackAdminService feedbackAdminService;

    /**
     * 分页查询反馈列表（支持状态筛选）
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询反馈列表")
    public ResultVo<?> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        IPage<Feedback> page = feedbackAdminService.pageList(pageNum, pageSize, status);
        return ResultUtils.success("查询成功", page);
    }

    /**
     * 管理员回复反馈
     */
    @PutMapping("/reply")
    @Operation(summary = "管理员回复反馈")
    public ResultVo<?> reply(
            @RequestParam Long id,
            @RequestParam String reply) {
        feedbackAdminService.replyFeedback(id, reply);
        return ResultUtils.success("回复成功");
    }

}
