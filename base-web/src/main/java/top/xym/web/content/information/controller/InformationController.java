package top.xym.web.content.information.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.content.information.entity.Information;
import top.xym.web.content.information.service.InformationService;
import top.xym.web.rag.utils.AliOssUtil;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/information")
@AllArgsConstructor
@Tag(name = "资讯管理模块")
public class InformationController {

    private final InformationService informationService;

    private final AliOssUtil aliOssUtil;

    /**
     * 分页查询资讯列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询资讯列表")
    public ResultVo<?> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title) {
        IPage<Information> page = informationService.pageList(pageNum, pageSize, title);
        return ResultUtils.success("查询成功", page);
    }

    /**
     * 资讯详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "资讯详情")
    public ResultVo<?> detail(@PathVariable Integer id) {
        Information info = informationService.getById(id);
        return ResultUtils.success("查询成功", info);
    }

    /**
     * 资讯封面图上传 OSS
     */
    @PostMapping("/cover/upload")
    @Operation(summary = "上传资讯封面图")
    public ResultVo<?> uploadInformationCover(@RequestParam("file")MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = "community/content/information/cover/" + aliOssUtil.generateUniqueFileName(originalFileName);
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        return ResultUtils.success("上传成功", url);
    }

    /**
     * 资讯内容图片上传 OSS
     */
    @PostMapping("/upload/image")
    @Operation(summary = "上传资讯内容图片")
    public ResultVo<?> uploadContentImage(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = "community/content/information/content/" + aliOssUtil.generateUniqueFileName(originalFileName);
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        return ResultUtils.success("上传成功", url);
    }

    /**
     * 新增资讯
     */
    @PostMapping("/add")
    @Operation(summary = "新增资讯")
    public ResultVo<?> add(@RequestBody Information information) {
        // 默认浏览量0
        information.setViewCount(0);
        information.setDeleted(0);
        informationService.save(information);
        return ResultUtils.success("新增成功");
    }

    /**
     * 修改资讯
     */
    @PutMapping("/update")
    @Operation(summary = "修改资讯")
    public ResultVo<?> update(@RequestBody Information information) {
        informationService.updateById(information);
        return ResultUtils.success("修改成功");
    }

    /**
     * 删除资讯（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除资讯")
    public ResultVo<?> delete(@PathVariable Integer id) {
        informationService.removeById(id);
        return ResultUtils.success("删除成功");
    }

    /**
     * 启用/禁用资讯
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "启用/禁用资讯")
    public ResultVo<?> updateStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        informationService.updateStatus(id, status);
        return ResultUtils.success("状态修改成功");
    }
}
