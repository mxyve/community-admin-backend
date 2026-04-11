package top.xym.web.shop.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.utils.SecurityUtils;
import top.xym.web.shop.order.entity.OrderPageParm;
import top.xym.web.shop.order.entity.ServiceOrder;
import top.xym.web.shop.order.service.ServiceOrderService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant/order")
@AllArgsConstructor
@Slf4j
@Tag(name = "shop服务订单管理模块")
public class ServiceOrderController {

    private final ServiceOrderService orderService;
    private final SysUserService sysUserService;

    private Integer getCurrentMerchantId() {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || user.getTenantId() == null) {
            throw new RuntimeException("当前用户不是租户管理员");
        }
        return user.getTenantId().intValue();
    }

    // ====================== 查询 ======================
    @PostMapping("/list")
    @Operation(summary = "租户端-订单列表")
    public ResultVo<?> list(@RequestBody OrderPageParm parm) {
        IPage<ServiceOrder> page = orderService.getTenantOrderList(parm, getCurrentMerchantId());
        return ResultUtils.success("查询成功", page);
    }

    @GetMapping("/statistics")
    @Operation(summary = "租户端-订单状态统计")
    public ResultVo<?> statistics() {
        Map<String, Integer> map = orderService.getOrderStatistics(getCurrentMerchantId());
        return ResultUtils.success("查询成功", map);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "租户端-订单详情")
    public ResultVo<?> detail(@PathVariable Integer id) {
        ServiceOrder order = orderService.getTenantDetail(id);
        return ResultUtils.success("查询成功", order);
    }

    // ====================== 操作 ======================
    @PostMapping("/accept/{id}")
    @Operation(summary = "接单")
    public ResultVo<?> accept(@PathVariable Integer id) {
        orderService.acceptOrder(id);
        return ResultUtils.success("接单成功");
    }

    @PostMapping("/reject/{id}")
    @Operation(summary = "拒单")
    public ResultVo<?> reject(
            @PathVariable Integer id,
            @RequestBody Map<String, String> params
    ) {
        String cancelReason = params.get("cancelReason");
        orderService.rejectOrder(id, cancelReason);
        return ResultUtils.success("拒单成功");
    }

    @PostMapping("/complete/{id}")
    @Operation(summary = "完成服务")
    public ResultVo<?> complete(@PathVariable Integer id) {
        orderService.completeOrder(id);
        return ResultUtils.success("服务已完成");
    }

    @PostMapping("/remindPay/{id}")
    @Operation(summary = "催付款")
    public ResultVo<?> remindPay(@PathVariable Integer id) {
        orderService.remindPay(id);
        return ResultUtils.success("已发送催付通知");
    }

    @PostMapping("/assign/{id}")
    @Operation(summary = "指派服务人员")
    public ResultVo<?> assign(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> params
    ) {
        Integer staffId = params.get("staffId");
        orderService.assignStaff(id, staffId);
        return ResultUtils.success("指派成功");
    }

    @PostMapping("/cancel/agree/{id}")
    @Operation(summary = "同意取消")
    public ResultVo<?> agreeCancel(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> params
    ) {
        String remark = params == null ? "" : params.get("remark");
        orderService.agreeCancel(id, remark);
        return ResultUtils.success("已同意取消订单");
    }

    @PostMapping("/cancel/reject/{id}")
    @Operation(summary = "拒绝取消")
    public ResultVo<?> rejectCancel(
            @PathVariable Integer id,
            @RequestBody Map<String, String> params
    ) {
        String rejectReason = params.get("rejectReason");
        orderService.rejectCancel(id, rejectReason);
        return ResultUtils.success("已拒绝取消申请");
    }

    @PostMapping("/refund/agree/{id}")
    @Operation(summary = "同意退款")
    public ResultVo<?> agreeRefund(
            @PathVariable Integer id,
            @RequestBody Map<String, String> params
    ) {
        BigDecimal refundAmount = new BigDecimal(params.get("refundAmount"));
        String refundReason = params.get("refundReason");
        orderService.agreeRefund(id, refundAmount, refundReason);
        return ResultUtils.success("退款成功");
    }

    @PostMapping("/refund/reject/{id}")
    @Operation(summary = "拒绝退款")
    public ResultVo<?> rejectRefund(
            @PathVariable Integer id,
            @RequestBody Map<String, String> params
    ) {
        String rejectReason = params.get("rejectReason");
        orderService.rejectRefund(id, rejectReason);
        return ResultUtils.success("已拒绝退款申请");
    }
}