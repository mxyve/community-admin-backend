package top.xym.web.shop.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("services")
public class Services {

    @TableId(type = IdType.AUTO)
    private Integer id;
    // 商家ID（关联 service_merchant 表）
    private Integer merchantId;
    // 所属分类ID（关联 service_category 表）
    private Integer categoryId;
    // 服务名称
    private String name;
    // 副标题
    private String subtitle;
    // 封面图
    private String coverImage;
    // 轮播图JSON
    private String bannerImages;
    // 详细介绍
    private String description;
    // 标签
    private String tags;
    // 计价类型
    private Integer priceType;
    // 基础价格
    private BigDecimal basePrice;
    // 原价
    private BigDecimal originalPrice;
    // 单位
    private String unit;
    // 最小购买数量
    private Integer minBuy;
    // 最大购买数量
    private Integer maxBuy;
    // 总销量
    private Integer totalSales;
    // 月销量
    private Integer monthlySales;
    // 评分
    private BigDecimal ratingScore;
    // 评价人数
    private Integer ratingCount;
    // 服务范围（JSON）
    private String serviceArea;
    // 是否热门
    private Integer isHot;
    // 是否推荐
    private Integer isRecommend;
    // 上下架状态：1上架 0下架
    private Integer status;
    // 省
    private String province;
    // 市
    private String city;
    // 区
    private String district;
    // 详细地址
    private String addressDetail;
    // 审核状态：0待审核 1审核通过 2审核不通过
    private Integer auditStatus;
    // 审核不通过原因
    private String auditReason;
    // 审核时间
    private Date auditTime;
    // 审核人ID（关联 sys_user）
    private Long auditorId;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}