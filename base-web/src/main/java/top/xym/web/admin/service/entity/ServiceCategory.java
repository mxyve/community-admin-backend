package top.xym.web.admin.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("service_category")
public class ServiceCategory {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 分类名称
    private String name;

    // 排序权重
    private Integer sort;

    // 是否显示：1显示 0隐藏
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}