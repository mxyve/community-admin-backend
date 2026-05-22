package top.xym.web.shop.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.xym.utils.SecurityUtils;
import top.xym.web.shop.order.entity.MerchantFinanceDTO;
import top.xym.web.shop.order.entity.ServiceOrder;
import top.xym.web.shop.order.mapper.ServiceOrderMapper;
import top.xym.web.shop.service.mapper.ShopServiceMapper;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final ServiceOrderMapper orderMapper;
    private final ShopServiceMapper shopServiceMapper;
    private final SysUserService sysUserService;

    /**
     * 获取当前商家ID
     */
    private Long getCurrentMerchantId() {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = sysUserService.getById(userId);
        if (user == null || user.getTenantId() == null) {
            throw new RuntimeException("当前用户不是租户管理员");
        }
        return user.getTenantId();
    }

    /**
     * 财务总览
     */
    public MerchantFinanceDTO getFinanceOverview() {
        Long merchantId = getCurrentMerchantId();

        BigDecimal completed = orderMapper.getTotalCompletedAmount(merchantId);
        completed = (completed == null) ? BigDecimal.ZERO : completed;

        BigDecimal refunded = orderMapper.getTotalRefundedAmount(merchantId);
        refunded = (refunded == null) ? BigDecimal.ZERO : refunded;

        MerchantFinanceDTO dto = new MerchantFinanceDTO();
        dto.setTotalTradeAmount(completed.add(refunded));
        dto.setTotalRefundAmount(refunded);
        dto.setTotalProfitAmount(completed);
        return dto;
    }

    /**
     * 财务订单列表（已完成 4 + 已退款 8）
     * 修复：不手动赋值 serviceName，避免报错
     */
    public IPage<ServiceOrder> getFinanceOrderList(Integer pageNum, Integer pageSize) {
        Long merchantId = getCurrentMerchantId();

        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceOrder::getMerchantId, merchantId)
                .eq(ServiceOrder::getDeleted, 0)
                .in(ServiceOrder::getStatus, 4, 8)
                .orderByDesc(ServiceOrder::getCreateTime);

        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        return orderMapper.selectPage(page, wrapper);
    }


}
