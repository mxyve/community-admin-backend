package top.xym.web.content.feedback.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("i_feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String content;

    // 图片，逗号分隔
    private String images;

    // 0=待处理 1=管理员已处理 3=用户已撤销
    private Integer status;

    private String reply;

    private LocalDateTime replyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickName;

    @TableField(exist = false)
    private String phone;
}