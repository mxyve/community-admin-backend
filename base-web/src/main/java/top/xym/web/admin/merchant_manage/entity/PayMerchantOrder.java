package top.xym.web.admin.merchant_manage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pay_merchant_order")
public class PayMerchantOrder {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 订单号
    private String orderNo;

    // 商家ID
    private Integer merchantId;

    // 套餐类型
    private String packageType;

    // 金额
    private String amount;

    // 支付状态 0未支付 1已支付
    private Integer payStatus;

    // 支付时间
    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}