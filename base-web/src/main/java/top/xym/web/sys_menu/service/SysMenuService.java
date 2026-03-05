package top.xym.web.sys_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.sys_menu.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getParent();
    // 根据用户id查询菜单
    List<SysMenu> getMenuByUserId(Long userId);
    // 根据角色id查询菜单id
    List<SysMenu> getMenuByRoleId(Long roleId);
}
