package top.xym.web.admin.community.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.xym.result.Result;
import top.xym.web.admin.community.entity.Tag;
import top.xym.web.admin.community.service.CommunityTagService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/community/tag")
@AllArgsConstructor
public class CommunityTagController {

    @Autowired
    private CommunityTagService communityTagService;

    /*
     * 分页查询分类列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param name 分类名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询分类列表")
    public Result<IPage<Tag>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<Tag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();

        // 只查询未删除的
        wrapper.eq(Tag::getDeleted, 0);

        // 按名称模糊查询
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Tag::getName, name);
        }

        // 按状态查询
        if (status != null) {
            wrapper.eq(Tag::getStatus, status);
        }

        IPage<Tag> result = communityTagService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取全部分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取全部分类列表")
    public Result<List<Tag>> list(
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDeleted, 0);

        if (status != null) {
            wrapper.eq(Tag::getStatus, status);
        }

        List<Tag> list = communityTagService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 新增分类
     */
    @PostMapping("/add")
    @Operation(summary = "新增分类")
    public Result<?> add(@RequestBody Tag tag) {
        // 检查名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName())
                .eq(Tag::getDeleted, 0);
        long count = communityTagService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        // 设置默认值
        tag.setStatus(tag.getStatus() != null ? tag.getStatus() : 1);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        tag.setDeleted(0);

        boolean saved = communityTagService.save(tag);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 修改分类
     */
    @PutMapping("/update")
    @Operation(summary = "修改分类")
    public Result<?> update(@RequestBody Tag tag) {
        if (tag.getTagId() == null) {
            return Result.error("分类ID不能为空");
        }

        // 检查名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, tag.getName())
                .eq(Tag::getDeleted, 0)
                .ne(Tag::getTagId, tag.getTagId());
        long count = communityTagService.count(wrapper);
        if (count > 0) {
            return Result.error("分类名称已存在");
        }

        tag.setUpdateTime(LocalDateTime.now());
        boolean updated = communityTagService.updateById(tag);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除分类")
    public Result<?> delete(@PathVariable Integer id) {
        // 检查该分类下是否有服务项目
        long count = communityTagService.countByTagId(id);
        if (count > 0) {
            return Result.error("该分类下存在 " + count + " 个文章，请先删除或转移文章后再删除分类");
        }

        boolean removed = communityTagService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 切换状态（启用/禁用）
     * @param id 分类ID
     * @param status 状态（1启用，0禁用）
     * @return 操作结果
     */
    @PutMapping("/toggleStatus")
    @Operation(summary = "切换状态（启用/禁用）")
    public Result<?> toggleStatus(@RequestParam Integer id, @RequestParam Integer status) {
        Tag tag = new Tag();
        tag.setTagId(id);
        tag.setStatus(status);
        tag.setUpdateTime(LocalDateTime.now());

        boolean updated = communityTagService.updateById(tag);
        return updated ? Result.success("状态修改成功") : Result.error("状态修改失败");
    }

}
