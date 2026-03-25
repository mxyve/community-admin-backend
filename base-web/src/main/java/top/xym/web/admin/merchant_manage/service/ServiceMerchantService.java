package top.xym.web.admin.merchant_manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.admin.merchant_manage.entity.MerchantPageParm;
import top.xym.web.admin.merchant_manage.entity.ServiceMerchant;

public interface ServiceMerchantService extends IService<ServiceMerchant> {

    // 商家入驻申请
    void applyMerchant(ServiceMerchant merchant);

    // 租户管理-分页列表
    IPage<ServiceMerchant> getMerchantList(MerchantPageParm parm);

    // 更新租户状态
    void updateStatus(Long id, Integer applyStatus, String applyDesc);
}
