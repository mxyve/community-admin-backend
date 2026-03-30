package top.xym.web.shop.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.shop.service.entity.ServiceStaff;
import top.xym.web.shop.service.entity.StaffPageParm;

public interface ServiceStaffService extends IService<ServiceStaff> {

    // 租户端-分页查询服务人员列表
    IPage<ServiceStaff> getTenantStaffList(StaffPageParm parm, Integer merchantId);

    // 租户端-新增服务人员
    void tenantCreate(ServiceStaff staff);

    // 租户端-修改服务人员
    void tenantUpdate(ServiceStaff staff);

    // 租户端-删除服务人员（逻辑删除）
    void tenantDelete(Integer id);

    // 租户端-获取服务人员详情
    ServiceStaff getTenantDetail(Integer id);
}
