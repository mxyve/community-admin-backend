package top.xym.web.admin.merchant_manage.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.config.AliPayConfig;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.admin.merchant_manage.entity.MerchantPageParm;
import top.xym.web.admin.merchant_manage.entity.PayMerchantOrder;
import top.xym.web.admin.merchant_manage.entity.ServiceMerchant;
import top.xym.web.admin.merchant_manage.mapper.PayMerchantOrderMapper;
import top.xym.web.admin.merchant_manage.mapper.ServiceMerchantMapper;
import top.xym.web.admin.merchant_manage.service.ServiceMerchantService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import java.util.Date;
import java.util.Map;

@Service
@AllArgsConstructor
public class ServiceMerchantServiceImpl extends ServiceImpl<ServiceMerchantMapper, ServiceMerchant> implements ServiceMerchantService {

    private final SysUserService sysUserService;
    private final ServiceMerchantMapper serviceMerchantMapper;
    private final AliPayConfig aliPayConfig;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 支付宝固定网关（沙箱）
    private static final String GATEWAY = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    private final PayMerchantOrderMapper payMerchantOrderMapper;

    // 商家入驻申请
    @Transactional
    @Override
    public void applyMerchant(ServiceMerchant merchant) {

        // 支付校验
        String packageType = merchant.getPackageType();
        if ("专业版".equals(packageType) || "旗舰版".equals(packageType)) {
            // 收费套餐必须支付成功
            if (merchant.getPayStatus() == null || merchant.getPayStatus() != 1) {
                throw new RuntimeException("请完成支付宝支付后再提交入驻申请");
            }
        } else {
            // 基础版默认已支付
            merchant.setPayStatus(1);
        }

        // 1.插入商家信息，状态默认待审核 0
        String merchantCode = "M" + System.currentTimeMillis() + (int)(Math.random() * 900 + 100);
        merchant.setMerchantCode(merchantCode);
        merchant.setApplyStatus(0);
        // 3=待审核（营业状态）
        // merchant.setStatus(3);
        merchant.setApplyTime(new Date());
        merchant.setDeleted(0);
        // 默认服务0次
        merchant.setServiceCount(0);
        // 默认响应时间10分钟
        merchant.setResponseTime(10);
        this.baseMapper.insert(merchant);

        // 2.自动创建租户管理员账号（sys_user）
        SysUser user = new SysUser();
        user.setUsername(merchant.getContactPhone());
        user.setPassword(passwordEncoder.encode(merchant.getPassword()));
        user.setPhone(merchant.getContactPhone());
        user.setNickName(merchant.getContactPerson());
        user.setEmail(merchant.getEmail());
        // 绑定租户ID
        user.setTenantId(merchant.getId());
        user.setDeleted(0);
        sysUserService.save(user);
    }

    // 租户管理-分页查询
    @Override
    public IPage<ServiceMerchant> getMerchantList(MerchantPageParm parm) {
        IPage<ServiceMerchant> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<ServiceMerchant> query = new QueryWrapper<>();
        // 条件查询
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().like(ServiceMerchant::getName, parm.getName());
        }
        if (StringUtils.isNotEmpty(parm.getContactPhone())) {
            query.lambda().like(ServiceMerchant::getContactPhone, parm.getContactPhone());
        }
        if (StringUtils.isNotEmpty(parm.getPackageType())) {
            query.lambda().eq(ServiceMerchant::getPackageType, parm.getPackageType());
        }
        if (parm.getApplyStatus() != null) {
            query.lambda().eq(ServiceMerchant::getApplyStatus, parm.getApplyStatus());
        }
        query.lambda().orderByDesc(ServiceMerchant::getApplyTime);
        return this.baseMapper.selectPage(page, query);
    }

    @Transactional
    @Override
    public void updateStatus(Long id, Integer applyStatus, String applyDesc) {
        // 1. 查询租户是否存在
        ServiceMerchant merchant = this.baseMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("租户不存在");
        }

        // 2. 状态校验
        // 待审核状态只能通过审核或驳回
        if (merchant.getApplyStatus() == 0 && applyStatus != 1 && applyStatus != 2) {
            throw new RuntimeException("待审核状态只能通过审核或驳回");
        }

        // 已开通状态只能禁用
        if (merchant.getApplyStatus() == 1 && applyStatus != 3) {
            throw new RuntimeException("已开通状态只能禁用");
        }

        // 已禁用状态只能启用
        if (merchant.getApplyStatus() == 3 && applyStatus != 1) {
            throw new RuntimeException("已禁用状态只能启用");
        }

        // 3. 更新状态
        merchant.setApplyStatus(applyStatus);

        // 4. 如果是驳回，保存驳回原因
        if (applyStatus == 2) {
            if (StringUtils.isEmpty(applyDesc)) {
                throw new RuntimeException("驳回时必须填写驳回原因");
            }
            merchant.setApplyDesc(applyDesc);
        }

        // 5. 如果是审核通过，设置审核通过时间
        if (applyStatus == 1 && merchant.getApplyStatus() == 0) {
            merchant.setAuditTime(new Date());
        }

        // 6. 设置审核人ID（可以从当前登录用户获取）
        // merchant.setAuditUserId(currentUserId);

        // 7. 更新数据库
        this.baseMapper.updateById(merchant);

    }

    // ===================== 支付宝生成二维码（当面付-扫码支付） =====================
    @Override
    public ResultVo createAlipayQrCode(String packageType, String orderNo) {
        try {
            int amount = "专业版".equals(packageType) ? 99 : 299;

            // 1. 先创建订单记录
            PayMerchantOrder order = new PayMerchantOrder();
            order.setOrderNo(orderNo);
            order.setPackageType(packageType);
            order.setAmount(String.valueOf(amount));
            order.setPayStatus(0);
            payMerchantOrderMapper.insert(order);

            // 2. 调用支付宝生成二维码
            AlipayClient client = new DefaultAlipayClient(
                    GATEWAY,
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    FORMAT, CHARSET,
                    aliPayConfig.getAlipayPublicKey(),
                    SIGN_TYPE
            );

            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());

            String biz = String.format(
                    "{\"out_trade_no\":\"%s\",\"total_amount\":\"%s\",\"subject\":\"%s套餐入驻\"}",
                    orderNo, amount, packageType
            );
            request.setBizContent(biz);

            AlipayTradePrecreateResponse response = client.execute(request);

            if (response.isSuccess()) {
                return ResultUtils.success("ok", response.getQrCode());
            } else {
                return ResultUtils.error("支付创建失败：" + response.getSubMsg());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("支付创建异常：" + e.getMessage());
        }
    }

    // ===================== 支付宝支付回调 =====================
    @Override
    public String payNotify(HttpServletRequest request) {
        try {
            String outTradeNo = request.getParameter("out_trade_no");
            String tradeStatus = request.getParameter("trade_status");

            System.out.println("收到支付宝回调：outTradeNo=" + outTradeNo + ", tradeStatus=" + tradeStatus);

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                // 更新订单表支付状态
                int result = payMerchantOrderMapper.updatePayStatus(outTradeNo);
                System.out.println("更新订单支付状态结果：" + result);
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    // 查询支付状态
    @Override
    public ResultVo getPayStatus(String orderNo) {
        Integer payStatus = payMerchantOrderMapper.getPayStatus(orderNo);
        if (payStatus != null && payStatus == 1) {
            return ResultUtils.success("已支付", true);
        }
        return ResultUtils.success("未支付", false);
    }

}