package top.xym.web.sys_menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.xym.web.sys_menu.entity.MakeMenuTree;
import top.xym.web.sys_menu.entity.SysMenu;
import top.xym.web.sys_menu.mapper.SysMenuMapper;
import top.xym.web.sys_menu.service.SysMenuService;

import java.util.Arrays;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Override
    public List<SysMenu> getParent() {
        String[] type = {"0", "1"};
        List<String> strings = Arrays.asList(type);
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().in(SysMenu::getType, strings).orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menuList = this.baseMapper.selectList(query);
        // 组装顶级树
        SysMenu menu = new SysMenu();
        menu.setTitle("顶级菜单");
        menu.setLabel("顶级菜单");
        menu.setParentId(-1L);
        menu.setMenuId(0L);
        menuList.add(menu);
        // 组装菜单树
        return MakeMenuTree.makeTree(menuList, -1L);
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        // 1. 查询菜单
        List<SysMenu> menuList = this.baseMapper.getMenuByUserId(userId);
        // 2. 组装成树形（必须加！）
        return MakeMenuTree.makeTree(menuList, 0L);
    }


    @Override
    public List<SysMenu> getMenuByRoleId(Long roleId) {
        return this.baseMapper.getMenuByRoleId(roleId);
    }
}
