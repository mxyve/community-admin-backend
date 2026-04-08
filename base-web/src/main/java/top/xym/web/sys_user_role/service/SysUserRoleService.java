package top.xym.web.sys_user_role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.sys_user_role.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole>{
    Long getRoleIdsByUserId(Long userId);
}
