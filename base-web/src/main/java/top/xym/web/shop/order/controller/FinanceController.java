package top.xym.web.shop.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.shop.order.entity.MerchantFinanceDTO;
import top.xym.web.shop.order.service.FinanceService;

@RestController
@RequestMapping("/api/tenant/finance")
@RequiredArgsConstructor
@Tag(name = "财务管理")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/overview")
    @Operation(summary = "财务总览")
    public ResultVo<?> overview() {
        MerchantFinanceDTO data = financeService.getFinanceOverview();
        return ResultUtils.success("查询成功", data);
    }

    @GetMapping("/orderPage")
    @Operation(summary = "财务订单列表")
    public ResultVo<?> orderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ResultUtils.success("查询成功", financeService.getFinanceOrderList(pageNum, pageSize));
    }

}