package top.xym.web.admin.merchant_manage.entity;

import lombok.Data;

@Data
public class MerchantPageParm {
    private Long currentPage;
    private Long pageSize;
    private String name;
    private String contactPhone;
    // 新增：套餐类型
    private String packageType;
    // 新增：审核状态
    private Integer applyStatus;

}