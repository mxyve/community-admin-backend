package top.xym.web.sys_menu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.sys_menu.entity.MakeMenuTree;
import top.xym.web.sys_menu.entity.SysMenu;
import top.xym.web.sys_menu.service.SysMenuService;

import java.util.List;

@RequestMapping("/api/sysMenu")
@RestController
@AllArgsConstructor
public class SysMenuController {
    private final SysMenuService sysMenuService;

    // 新增
    @PostMapping
    @Operation(summary = "新增菜单")
    public ResultVo<?> add(@RequestBody SysMenu sysMenu) {
        if (sysMenuService.save(sysMenu)) {
            return ResultUtils.success("新增成功！");
        }
        return ResultUtils.error("新增失败！");
    }

    // 编辑
    @PutMapping
    @Operation(summary = "编辑菜单")
    public ResultVo<?> edit(@RequestBody SysMenu sysMenu) {
        if (sysMenuService.updateById(sysMenu)) {
            return ResultUtils.success("编辑成功！");
        }
        return ResultUtils.error("编辑失败！");
    }

    // 删除
    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除菜单")
    public ResultVo<?> delete(@PathVariable("menuId") Long menuId) {
        if (sysMenuService.removeById(menuId)) {
            return ResultUtils.success("删除成功！");
        }
        return ResultUtils.error("删除失败！");
    }

    // 列表
    @GetMapping("/list")
    @Operation(summary = "菜单列表")
    public ResultVo<?> getList() {
        // 排序
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.lambda().orderByAsc(SysMenu::getOrderNum);
        // 查询出所有的菜单
        List<SysMenu> list = sysMenuService.list(query);
        // 组装树数据
        List<SysMenu> menuList = MakeMenuTree.makeTree(list, 0L);
        return ResultUtils.success("查询成功", menuList);
    }

    // 上级菜单
    @GetMapping("/getParent")
    @Operation(summary = "上级菜单")
    public ResultVo<?> getParent() {
        List<SysMenu> list = sysMenuService.getParent();
        return ResultUtils.success("查询成功", list);
    }

}
