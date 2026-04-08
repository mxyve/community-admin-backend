package top.xym.web.admin.merchant_manage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("service_merchant")
public class ServiceMerchant {

    @TableId(type = IdType.AUTO)
    // 商家ID（租户ID）
    private Long id;
    // 商家全称
    private String name;
    // 对外展示名称
    private String displayName;
    // 联系人姓名
    private String contactPerson;
    // 联系电话
    private String contactPhone;
    // 邮箱
    private String email;
    // 营业执照号/图片
    private String businessLicense;
    // 法人身份证图片
    private String legalIdCardImg;
    // 商家介绍/经营范围
    private String intro;
    // 服务类型
    private String serviceType;
    // 套餐类型
    private String packageType;
    // 到期时间
    private LocalDateTime expireTime;
    // 分佣比例
    private BigDecimal profitRatio;
    // 入驻状态：0待审核 1通过 2驳回 3禁用
    private Integer applyStatus;
    // 审核备注/驳回原因
    private String applyDesc;
    // 申请时间
    private Date applyTime;
    // 审核人ID
    private Long auditUserId;
    // 审核时间
    private Date auditTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}