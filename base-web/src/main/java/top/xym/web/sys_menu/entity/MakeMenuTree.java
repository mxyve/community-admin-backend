package top.xym.web.sys_menu.entity;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MakeMenuTree {
    //组装树工具
    public static List<SysMenu> makeTree(List<SysMenu> menuList, Long pid) {
        // 存放组装的树数据
        List<SysMenu> list = new ArrayList<>();
        // 组装树
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item -> {
                    SysMenu menu = new SysMenu();
                    BeanUtils.copyProperties(item, menu);
                    menu.setLabel(item.getTitle());
                    menu.setValue(item.getMenuId());
                    // 查找下级：递归调用；自己调用自己
                    List<SysMenu> children = makeTree(menuList, item.getMenuId());
                    menu.setChildren(children);
                    list.add(menu);
                });
        return list;
    }
}
