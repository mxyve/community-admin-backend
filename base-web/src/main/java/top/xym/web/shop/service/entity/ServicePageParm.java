package top.xym.web.shop.service.entity;

import lombok.Data;

@Data
public class ServicePageParm {
    private Long currentPage;
    private Long pageSize;
    // 服务名称
    private String name;
    // 分类ID
    private Integer categoryId;
    // 上下架状态
    private Integer status;
    // 审核状态
    private Integer auditStatus;
}