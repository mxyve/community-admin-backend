package top.xym.web.admin.merchant_manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.admin.merchant_manage.entity.MerchantPageParm;
import top.xym.web.admin.merchant_manage.entity.ServiceMerchant;
import top.xym.web.admin.merchant_manage.entity.dto.StatusUpdateDto;
import top.xym.web.admin.merchant_manage.service.ServiceMerchantService;

@RestController
@RequestMapping("/api/serviceMerchant")
@AllArgsConstructor
public class ServiceMerchantController {

    private final ServiceMerchantService serviceMerchantService;

    // 商家入驻
    @PostMapping("/apply")
    @Operation(summary = "商家入驻申请")
    public ResultVo<?> apply(@RequestBody ServiceMerchant merchant) {
        serviceMerchantService.applyMerchant(merchant);
        return ResultUtils.success("入驻申请提交成功，请等待审核！");
    }

    // 租户管理-列表
    @PostMapping("/list")
    @Operation(summary = "租户管理-列表")
    public ResultVo<?> list(@RequestBody MerchantPageParm parm) {
        IPage<ServiceMerchant> list = serviceMerchantService.getMerchantList(parm);
        return ResultUtils.success("查询成功", list);
    }

    // 编辑
    @PutMapping
    @Operation(summary = "编辑租户信息")
    public ResultVo<?> edit(@RequestBody ServiceMerchant merchant) {
        serviceMerchantService.updateById(merchant);
        return ResultUtils.success("编辑成功");
    }

    // 删除
    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户信息")
    public ResultVo<?> delete(@PathVariable Long id) {
        serviceMerchantService.removeById(id);
        return ResultUtils.success("删除成功");
    }

    // 更新租户状态接口
    @PutMapping("/status")
    @Operation(summary = "更新租户状态")
    public ResultVo<?> updateStatus(@RequestBody StatusUpdateDto request) {
        serviceMerchantService.updateStatus(request.getId(), request.getApplyStatus(), request.getApplyDesc());
        return ResultUtils.success("更新成功");
    }
}
