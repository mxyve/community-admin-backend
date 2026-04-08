package top.xym.web.shop.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.shop.service.entity.ServicePageParm;
import top.xym.web.shop.service.entity.Services;

public interface ShopServiceService extends IService<Services> {

    // 租户端-分页查询服务列表（多条件筛选）
    IPage<Services> getTenantServiceList(ServicePageParm parm, Integer merchantId);

    // 租户端-新增服务（自动设为待审核状态）
    void tenantCreate(Services service);

    // 租户端-修改服务（修改后重置为待审核状态）
    void tenantUpdate(Services service);

    // 租户端-删除服务（逻辑删除）
    void tenantDelete(Integer id);

    // 租户端-主动下架已上架服务
    void tenantOffline(Integer id);

    // 租户端-上架服务
    void tenantApplyOnline(Integer id);

    // 租户端-获取服务详情
    Services getTenantDetail(Integer id);
}