package top.xym.web.admin.merchant_manage.entity;

import lombok.Data;

@Data
public class MerchantPageParm {
    private Long currentPage;
    private Long pageSize;
    private String name;
    private String contactPhone;
}