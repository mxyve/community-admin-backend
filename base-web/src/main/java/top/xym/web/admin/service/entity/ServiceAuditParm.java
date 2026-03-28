package top.xym.web.admin.service.entity;

import lombok.Data;

@Data
public class ServiceAuditParm {
    private Integer id;
    // 审核不通过原因
    private String auditReason;
}