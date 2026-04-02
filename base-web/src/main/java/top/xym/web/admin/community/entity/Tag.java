package top.xym.web.admin.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    // 商家ID（租户ID）
    private Integer tagId;

    // 标签名
    private String name;

    // 图标
    private String icon;

    // 颜色
    private String color;

    // 状态 0：审核不通过，1：审核通过 2：审核中
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
