package top.xym.web.shop.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.shop.order.entity.OrderPageParm;
import top.xym.web.shop.order.entity.ServiceOrder;

import java.math.BigDecimal;
import java.util.Map;

public interface ServiceOrderService extends IService<ServiceOrder> {

    // 租户端-分页查询订单列表
    IPage<ServiceOrder> getTenantOrderList(OrderPageParm parm, Integer merchantId);

    // 租户端-订单状态统计（tab角标）
    Map<String, Integer> getOrderStatistics(Integer merchantId);

    // 租户端-订单详情
    ServiceOrder getTenantDetail(Integer id);

    // 接单
    void acceptOrder(Integer id);

    // 拒单
    void rejectOrder(Integer id, String cancelReason);

    // 完成服务
    void completeOrder(Integer id);

    // 催付款
    void remindPay(Integer id);

    // 指派服务人员
    void assignStaff(Integer id, Integer staffId);

    // 同意取消
    void agreeCancel(Integer id, String remark);

    // 拒绝取消
    void rejectCancel(Integer id, String rejectReason);

    // 同意退款
    void agreeRefund(Integer id, BigDecimal refundAmount, String refundReason);

    // 拒绝退款
    void rejectRefund(Integer id, String rejectReason);
}