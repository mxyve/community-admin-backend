package top.xym.web.admin.service.entity;

import lombok.Data;

@Data
public class AdminServiceParm {
    private Long currentPage;
    private Long pageSize;
    // 服务名称
    private String name;
    // 分类
    private Integer categoryId;
    // 状态
    private Integer status;
    // 审核状态
    private Integer auditStatus;
}