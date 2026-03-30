package top.xym.web.shop.order.entity;

import lombok.Data;

@Data
public class OrderPageParm {
    // 当前页
    private long currentPage;
    // 每页条数
    private long pageSize;
    // 订单号
    private String orderNo;
    // 服务名称
    private String serviceTitle;
    // 用户手机号
    private String userMobile;
    // 订单状态
    private Integer status;
    // 时间范围：today/week/month/all
    private String timeRange;
}