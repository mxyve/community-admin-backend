package top.xym.web.shop.service.entity;

import lombok.Data;

@Data
public class StaffPageParm {
    private Long currentPage;
    private Long pageSize;
    // 姓名/手机号
    private String name;
    // 服务区域
    private String serviceArea;
    // 状态
    private Integer status;
}
