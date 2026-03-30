package top.xym.web.shop.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("service_orders")
public class ServiceOrder {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 商家ID（租户ID）
    @TableField("merchant_id")
    private Integer merchantId;

    // 订单编号（唯一）
    @TableField("order_no")
    private String orderNo;

    // 用户ID
    @TableField("user_id")
    private Long userId;

    // 服务ID
    @TableField("service_id")
    private Integer serviceId;

    // 服务人员ID
    @TableField("staff_id")
    private Integer staffId;

    // 规格ID
    @TableField("spec_id")
    private Integer specId;

    // 数量
    private Integer quantity;

    // 单价
    @TableField("unit_price")
    private BigDecimal unitPrice;

    // 订单总金额
    @TableField("total_amount")
    private BigDecimal totalAmount;

    // 优惠金额
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    // 实付金额
    @TableField("pay_amount")
    private BigDecimal payAmount;

    // 支付方式：1微信2支付宝3余额
    @TableField("payment_method")
    private Integer paymentMethod;

    // 支付时间
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    // 订单状态：1待服务2服务中3待付款4已完成5取消申请中6已取消7退款中8已退款
    private Integer status;

    // 预约服务时间
    @TableField("service_time")
    private LocalDateTime serviceTime;

    // 服务地址
    @TableField("service_address")
    private String serviceAddress;

    // 联系人
    @TableField("contact_name")
    private String contactName;

    // 联系电话
    @TableField("contact_phone")
    private String contactPhone;

    // 用户备注
    @TableField("user_remark")
    private String userRemark;

    // 商家备注
    @TableField("merchant_remark")
    private String merchantRemark;

    // 取消原因
    @TableField("cancel_reason")
    private String cancelReason;

    // 退款原因
    @TableField("refund_reason")
    private String refundReason;

    // 退款金额
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    // 完成时间
    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String serviceTitle;  // 服务名称（展示用）

    @TableField(exist = false)
    private String userMobile;   // 用户手机号（展示用）

    @TableField(exist = false)
    private String staffName;    // 服务人员姓名
}