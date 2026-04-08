package top.xym.web.admin.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.admin.service.entity.AdminServiceParm;
import top.xym.web.admin.service.entity.ServiceAuditParm;
import top.xym.web.admin.service.entity.Services;
import top.xym.web.admin.service.service.ServiceItemService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/service")
@AllArgsConstructor
public class ServiceItemController {


    private final ServiceItemService adminShopServiceService;

    // 超管分页查询
    @PostMapping("/list")
    @Operation(summary = "超管-服务审核列表")
    public ResultVo<?> list(@RequestBody AdminServiceParm parm) {
        IPage<Services> page = adminShopServiceService.getAdminServiceList(parm);
        return ResultUtils.success("查询成功",page);
    }

    // 审核通过
    @PostMapping("/auditPass")
    @Operation(summary = "超管-审核通过")
    public ResultVo<?> auditPass(@RequestBody ServiceAuditParm parm) {
        adminShopServiceService.auditPass(parm);
        return ResultUtils.success("审核通过");
    }

    // 审核不通过
    @PostMapping("/auditReject")
    @Operation(summary = "超管-审核不通过")
    public ResultVo<?> auditReject(@RequestBody ServiceAuditParm parm) {
        adminShopServiceService.auditReject(parm);
        return ResultUtils.success("已驳回");
    }

    // 重新审核（将服务恢复为待审核状态）
    @PostMapping("/reAudit")
    @Operation(summary = "超管 - 重新审核")
    public ResultVo<?> reAudit(@RequestBody Map<String, Integer> map) {
        adminShopServiceService.reAudit(map.get("id"));
        return ResultUtils.success("已重新提交审核");
    }

    // 详情
    @GetMapping("/detail/{id}")
    @Operation(summary = "超管-服务详情")
    public ResultVo<?> detail(@PathVariable Integer id) {
        Services service = adminShopServiceService.getAdminDetail(id);
        return ResultUtils.success("查询成功",service);
    }
}
