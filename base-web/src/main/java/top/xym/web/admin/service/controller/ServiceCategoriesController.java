package top.xym.web.admin.service.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xym.result.Result;
import top.xym.web.admin.service.service.ServiceCategoriesService;
import top.xym.web.admin.service.entity.ServiceCategory;
import top.xym.web.admin.service.service.ServiceItemService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@AllArgsConstructor
public class ServiceCategoriesController {

    @Autowired
    private ServiceCategoriesService categoryService;

    @Autowired
    private ServiceItemService serviceItemService;

    /**
     * 分页查询分类列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param name 分类名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询分类列表")
    public Result<IPage<ServiceCategory>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        Page<ServiceCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();

        // 只查询未删除的
        wrapper.eq(ServiceCategory::getDeleted, 0);

        // 按名称模糊查询
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(ServiceCategory::getName, name);
        }

        // 按状态查询
        if (status != null) {
            wrapper.eq(ServiceCategory::getStatus, status);
        }

        // 按排序权重升序
        wrapper.orderByAsc(ServiceCategory::getSort);

        IPage<ServiceCategory> result = categoryService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取全部分类列表（用于下拉选择）
     * @return 分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取全部分类列表")
    public Result<List<ServiceCategory>> list(
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getDeleted, 0);

        if (status != null) {
            wrapper.eq(ServiceCategory::getStatus, status);
        }

        wrapper.orderByAsc(ServiceCategory::getSort);
        List<ServiceCategory> list = categoryService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增分类
     * @param category 分类信息
     * @return 操作结果
     */
    @PostMapping("/add")
    @Operation(summary = "新增分类")
    public Result<?> add(@RequestBody ServiceCategory category) {
        // 检查名称是否已存在
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getName, category.getName())
                .eq(ServiceCategory::getDeleted, 0);
        long count = categoryService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        // 设置默认值
        category.setStatus(category.getStatus() != null ? category.getStatus() : 1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setDeleted(0);

        boolean saved = categoryService.save(category);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改分类
     * @param category 分类信息
     * @return 操作结果
     */
    @PutMapping("/update")
    @Operation(summary = "修改分类")
    public Result<?> update(@RequestBody ServiceCategory category) {
        if (category.getId() == null) {
            return Result.error("分类ID不能为空");
        }

        // 检查名称是否已存在（排除自身）
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceCategory::getName, category.getName())
                .eq(ServiceCategory::getDeleted, 0)
                .ne(ServiceCategory::getId, category.getId());
        long count = categoryService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        category.setUpdateTime(LocalDateTime.now());
        boolean updated = categoryService.updateById(category);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除分类（软删除）
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除分类（软删除）")
    public Result<?> delete(@PathVariable Integer id) {
        // 检查该分类下是否有服务项目
        long count = serviceItemService.countByCategoryId(id);
        if (count > 0) {
            return Result.error("该分类下存在 " + count + " 个服务项目，请先删除或转移服务项目后再删除分类");
        }

        boolean removed = categoryService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

//    /**
//     * 批量删除
//     * @param ids 分类ID列表
//     * @return 操作结果
//     */
//    @DeleteMapping("/batchDelete")
//    @Operation(summary = "批量删除")
//    public Result<?> batchDelete(@RequestBody List<Integer> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return Result.error("请选择要删除的分类");
//        }
//
//        // 批量软删除
//        boolean removed = categoryService.removeByIds(ids);
//        return removed ? Result.success("批量删除成功") : Result.error("批量删除失败");
//    }

    /**
     * 切换状态（启用/禁用）
     * @param id 分类ID
     * @param status 状态（1启用，0禁用）
     * @return 操作结果
     */
    @PutMapping("/toggleStatus")
    @Operation(summary = "切换状态（启用/禁用）")
    public Result<?> toggleStatus(@RequestParam Integer id, @RequestParam Integer status) {
        ServiceCategory category = new ServiceCategory();
        category.setId(id);
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());

        boolean updated = categoryService.updateById(category);
        return updated ? Result.success("状态修改成功") : Result.error("状态修改失败");
    }

}