package top.xym.web.sys_role.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO)
    // 角色id
    private Long roleId;
    // 角色名称
    private String roleName;
    // 类型
    private String type;
    // 描述
    private String remark;
    // 逻辑删除字段
    @TableLogic
    private Integer deleted;

    // 自动填充创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 自动填充更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
