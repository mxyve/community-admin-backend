package top.xym.web.shop.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.shop.service.entity.ServiceCategory;
import top.xym.web.shop.service.service.ServiceCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/category")
@AllArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService categoryService;

    // 查询所有可用服务分类（给前端下拉框/选择器用）
    @GetMapping("/list")
    @Operation(summary = "查询可用服务分类列表")
    public ResultVo<?> list() {
        List<ServiceCategory> list = categoryService.getAllAvailableCategories();
        return ResultUtils.success("查询成功", list);
    }
}