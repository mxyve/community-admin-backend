package top.xym.web.shop.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.web.shop.service.entity.ServiceStaff;
import top.xym.web.shop.service.entity.StaffPageParm;
import top.xym.web.shop.service.mapper.ServiceStaffMapper;
import top.xym.web.shop.service.service.ServiceStaffService;

@Service
@AllArgsConstructor
public class ServiceStaffServiceImpl extends ServiceImpl<ServiceStaffMapper, ServiceStaff> implements ServiceStaffService {

    // 租户端-分页查询服务人员列表
    @Override
    public IPage<ServiceStaff> getTenantStaffList(StaffPageParm parm, Integer merchantId) {
        IPage<ServiceStaff> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<ServiceStaff> query = new QueryWrapper<>();

        // 租户数据隔离：只查当前商家的服务人员
        query.lambda().eq(ServiceStaff::getMerchantId, merchantId);

        // 条件筛选
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().and(w -> w.like(ServiceStaff::getName, parm.getName())
                    .or().like(ServiceStaff::getPhone, parm.getName()));
        }
        if (StringUtils.isNotEmpty(parm.getServiceArea())) {
            // JSON字段模糊查询（MySQL语法）
            query.lambda().like(ServiceStaff::getServiceAreas, parm.getServiceArea());
        }
        if (parm.getStatus() != null) {
            query.lambda().eq(ServiceStaff::getStatus, parm.getStatus());
        }

        query.lambda().orderByDesc(ServiceStaff::getCreateTime);
        return this.baseMapper.selectPage(page, query);
    }

    // 租户端-新增服务人员
    @Transactional
    @Override
    public void tenantCreate(ServiceStaff staff) {
        staff.setDeleted(0);
        this.baseMapper.insert(staff);
    }

    // 租户端-修改服务人员
    @Transactional
    @Override
    public void tenantUpdate(ServiceStaff staff) {
        ServiceStaff exist = this.baseMapper.selectById(staff.getId());
        if (exist == null) {
            throw new RuntimeException("服务人员不存在");
        }
        this.baseMapper.updateById(staff);
    }

    // 租户端-删除服务人员
    @Transactional
    @Override
    public void tenantDelete(Integer id) {
        ServiceStaff staff = this.baseMapper.selectById(id);
        if (staff == null) {
            throw new RuntimeException("服务人员不存在");
        }
        this.baseMapper.deleteById(id);
    }

    // 租户端-获取服务人员详情
    @Override
    public ServiceStaff getTenantDetail(Integer id) {
        return this.baseMapper.selectById(id);
    }
}
