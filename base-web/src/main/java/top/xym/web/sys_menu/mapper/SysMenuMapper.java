package top.xym.web.sys_menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.xym.web.sys_menu.entity.SysMenu;

import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    // 根据用户id查询菜单
    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);
    // 根据角色id查询菜单
    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);
}
