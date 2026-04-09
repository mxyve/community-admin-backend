package top.xym.web.sys_user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String nickName;
    private String avatar;
    private String phone;
    private String email;
    private String gender;
    private String isAdmin;
    private String province;
    private String city;
    private String district;
    // 租户Id
    private Long tenantId;
    // 账户是否过期（1 未过期，0已过期）
    private boolean isAccountNonExpired = true;
    // 账户是否被锁定（1 未锁定，0已锁定）
    private boolean isAccountNonLocked = true;
    // 密码是否过期（1 未过期，0已过期）
    private boolean isCredentialsNonExpired = true;
    // 账户是否可用（1 可用，0不可用）
    private boolean isEnabled = true;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    // 不属于用户表的字段，需要排除
    @TableField(exist = false)
    private String roleId;
}
