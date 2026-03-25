package top.xym.web.admin.merchant_manage.entity.dto;

import lombok.Data;

/**
 * 租户状态更新请求参数
 */
@Data
public class StatusUpdateDto {

    /**
     * 租户ID
     */
    private Long id;

    /**
     * 状态：0待审核 1审核通过 2审核驳回 3禁用
     */
    private Integer applyStatus;

    /**
     * 驳回原因（状态为2时必填）
     */
    private String applyDesc;
}