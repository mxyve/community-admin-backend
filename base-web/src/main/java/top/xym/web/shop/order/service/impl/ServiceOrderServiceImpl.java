package top.xym.web.shop.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.web.shop.order.entity.OrderPageParm;
import top.xym.web.shop.order.entity.ServiceOrder;
import top.xym.web.shop.order.mapper.ServiceOrderMapper;
import top.xym.web.shop.order.service.ServiceOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements ServiceOrderService {

    private final ServiceOrderMapper orderMapper;

    // ====================== 列表 + 统计 ======================
    @Override
    public IPage<ServiceOrder> getTenantOrderList(OrderPageParm parm, Integer merchantId) {
        Page<ServiceOrder> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());

        // 保留租户隔离 + 逻辑删除
        QueryWrapper<ServiceOrder> query = new QueryWrapper<>();
        query.eq("o.merchant_id", merchantId);  // 租户隔离 ✅
        query.eq("o.deleted", 0);              // 逻辑删除 ✅

        // 状态筛选
        if (parm.getStatus() != null) {
            query.eq("o.status", parm.getStatus());
        }

        // 订单号
        if (StringUtils.isNotEmpty(parm.getOrderNo())) {
            query.like("o.order_no", parm.getOrderNo());
        }
        // 服务名称
        if (StringUtils.isNotEmpty(parm.getServiceTitle())) {
            query.like("s.name", parm.getServiceTitle());
        }
        // 用户手机
        if (StringUtils.isNotEmpty(parm.getUserMobile())) {
            query.like("u.phone", parm.getUserMobile());
        }

        // 时间范围
        if (StringUtils.isNotEmpty(parm.getTimeRange())) {
            LocalDateTime now = LocalDateTime.now();
            if ("today".equals(parm.getTimeRange())) {
                query.ge("o.create_time", now.toLocalDate().atStartOfDay());
            } else if ("week".equals(parm.getTimeRange())) {
                query.ge("o.create_time", now.minusDays(7));
            } else if ("month".equals(parm.getTimeRange())) {
                query.ge("o.create_time", now.minusDays(30));
            }
        }

        query.orderByDesc("o.create_time");

        // 调用联表查询（XML里写了关联服务表、用户表、服务人员表）
        return baseMapper.selectPageVo(page, query);
    }

    @Override
    public Map<String, Integer> getOrderStatistics(Integer merchantId) {
        Map<String, Integer> map = new HashMap<>();

        // 统计全部
        QueryWrapper<ServiceOrder> allQuery = new QueryWrapper<>();
        allQuery.lambda().eq(ServiceOrder::getMerchantId, merchantId);
        allQuery.lambda().eq(ServiceOrder::getDeleted, 0);
        map.put("all", (int) this.count(allQuery));

        // 统计每个状态
        for (int i = 1; i <= 8; i++) {
            QueryWrapper<ServiceOrder> qw = new QueryWrapper<>();
            qw.lambda().eq(ServiceOrder::getMerchantId, merchantId);
            qw.lambda().eq(ServiceOrder::getDeleted, 0);
            qw.lambda().eq(ServiceOrder::getStatus, i);
            map.put("status" + i, (int) this.count(qw));
        }

        return map;
    }

    @Override
    public ServiceOrder getTenantDetail(Integer id) {
        return this.getById(id);
    }

    // ====================== 订单操作 ======================
    @Transactional
    @Override
    public void acceptOrder(Integer id) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 1) throw new RuntimeException("订单状态不允许接单");
        order.setStatus(2);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void rejectOrder(Integer id, String cancelReason) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 1) throw new RuntimeException("订单状态不允许拒单");
        order.setStatus(6);
        order.setCancelReason(cancelReason);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void completeOrder(Integer id) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 2) throw new RuntimeException("只能完成服务中的订单");
        order.setStatus(3);
        order.setCompletedAt(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void remindPay(Integer id) {
        // 可对接消息推送，这里仅做标记
    }

    @Transactional
    @Override
    public void assignStaff(Integer id, Integer staffId) {
        ServiceOrder order = this.getById(id);
        if (order == null) throw new RuntimeException("订单不存在");
        order.setStaffId(staffId);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void agreeCancel(Integer id, String remark) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 5) throw new RuntimeException("非取消申请中订单");
        order.setStatus(6);
        order.setCancelReason(remark);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void rejectCancel(Integer id, String rejectReason) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 5) throw new RuntimeException("非取消申请中订单");
        order.setStatus(1);
        order.setCancelReason(rejectReason);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void agreeRefund(Integer id, BigDecimal refundAmount, String refundReason) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 7) throw new RuntimeException("非退款中订单");
        order.setStatus(8);
        order.setRefundAmount(refundAmount);
        order.setRefundReason(refundReason);
        this.updateById(order);
    }

    @Transactional
    @Override
    public void rejectRefund(Integer id, String rejectReason) {
        ServiceOrder order = this.getById(id);
        if (order == null || order.getStatus() != 7) throw new RuntimeException("非退款中订单");
        order.setStatus(4);
        order.setRefundReason(rejectReason);
        this.updateById(order);
    }
}