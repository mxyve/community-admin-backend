package top.xym.web.sys_role_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.sys_role_menu.entity.SaveMenuParm;
import top.xym.web.sys_role_menu.entity.SysRoleMenu;

public interface SysRoleMenuService extends IService<SysRoleMenu> {
    void saveRoleMenu(SaveMenuParm parm);
}