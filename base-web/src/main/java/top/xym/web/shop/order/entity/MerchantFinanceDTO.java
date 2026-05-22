package top.xym.web.shop.order.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantFinanceDTO {
    // 总交易额（4+8）
    private BigDecimal totalTradeAmount;
    // 总退款额（8）
    private BigDecimal totalRefundAmount;
    // 实际收益（4）
    private BigDecimal totalProfitAmount;
}