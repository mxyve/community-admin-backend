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
    // 商家编号
    private String merchantCode;
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
    // 密码
    private String password;
    // 服务区域JSON数组
    @TableField(updateStrategy  = FieldStrategy.ALWAYS)  // 强制更新，即使是空字符串
    private String serviceAreas;
    // 营业执照号/图片
    private String businessLicense;
    // 法人身份证图片
    private String legalIdCardImg;
    // 商家LOGO
    private String logo;
    // 商家介绍/经营范围
    private String intro;
    // 服务类型
    private String serviceType;
    // 套餐类型
    private String packageType;
    // 租户套餐类型"基础版/专业版/旗舰版"
    private Integer applyStatus;
    // 综合评分
    private BigDecimal ratingScore;
    // 服务总次数
    private Integer serviceCount;
    // 平均响应时间（分钟）
    private Integer responseTime;
    // 状态：1营业0休息2关闭3待审核
    private Integer status;
    // 申请时间
    private Date applyTime;
    // 审核人ID
    private Long auditUserId;
    // 审核时间
    private Date auditTime;
    // 审核通过时间
    private LocalDateTime verifiedAt;
    // 审核备注/驳回原因
    private String applyDesc;
    // 到期时间
    private LocalDateTime expireTime;
    // 商家配置JSON（费率、通知设置等）
    private String config;
    // 分佣比例
    private BigDecimal profitRatio;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}