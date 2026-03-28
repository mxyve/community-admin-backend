package top.xym.web.shop.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.web.shop.service.entity.ServicePageParm;
import top.xym.web.shop.service.entity.Services;
import top.xym.web.shop.service.mapper.ShopServiceMapper;
import top.xym.web.shop.service.service.ShopServiceService;

@Service
@AllArgsConstructor
public class ShopServiceServiceImpl extends ServiceImpl<ShopServiceMapper, Services> implements ShopServiceService {

    // 租户端-分页查询服务列表
    @Override
    public IPage<Services> getTenantServiceList(ServicePageParm parm, Integer merchantId) {
        IPage<Services> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        QueryWrapper<Services> query = new QueryWrapper<>();

        // 租户数据隔离：只查当前商家的服务
        query.lambda().eq(Services::getMerchantId, merchantId);

        // 条件筛选
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().like(Services::getName, parm.getName());
        }
        if (parm.getCategoryId() != null) {
            query.lambda().eq(Services::getCategoryId, parm.getCategoryId());
        }
        if (parm.getStatus() != null) {
            query.lambda().eq(Services::getStatus, parm.getStatus());
        }
        if (parm.getAuditStatus() != null) {
            query.lambda().eq(Services::getAuditStatus, parm.getAuditStatus());
        }

        query.lambda().orderByDesc(Services::getCreateTime);
        return this.baseMapper.selectPage(page, query);
    }

    // 租户端-新增服务
    @Transactional
    @Override
    public void tenantCreate(Services service) {
        // 3表示服务下架（不审核）
        service.setAuditStatus(3);
        // 0表示已下架
        service.setStatus(0);
        service.setDeleted(0);
        this.baseMapper.insert(service);
    }

    // 租户端-修改服务
    @Transactional
    @Override
    public void tenantUpdate(Services service) {
        // 校验服务是否存在
        Services exist = this.baseMapper.selectById(service.getId());
        if (exist == null) {
            throw new RuntimeException("服务不存在");
        }
        // 已下架、不审核
        service.setAuditStatus(3);
        service.setStatus(0);
        this.baseMapper.updateById(service);
    }

    // 租户端-删除服务
    @Transactional
    @Override
    public void tenantDelete(Integer id) {
        Services service = this.baseMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        // 只允许删除已下架的服务（status=0 且 auditStatus=3）
        if (service.getStatus() != 0 || service.getAuditStatus() != 3) {
            throw new RuntimeException("只有已下架的服务才能删除");
        }
        this.baseMapper.deleteById(id);
    }

    // 租户端-下架服务
    @Transactional
    @Override
    public void tenantOffline(Integer id) {
        Services service = this.baseMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        // 只有已下架的服务不能重复下架
        if (service.getStatus() == 0) {
            throw new RuntimeException("服务已是下架状态，无需重复下架");
        }
        // 下架后状态变为：已下架、不审核
        service.setStatus(0);
        service.setAuditStatus(3);
        this.baseMapper.updateById(service);
    }

    // 租户端-申请上架
    @Transactional
    @Override
    public void tenantApplyOnline(Integer id) {
        Services service = this.baseMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        // 只有已下架的服务才能申请上架
        if (service.getStatus() != 0) {
            throw new RuntimeException("只有已下架的服务才能申请上架");
        }
        // 申请上架后状态变为：申请上架中、审核中
        service.setStatus(2);
        service.setAuditStatus(2);
        this.baseMapper.updateById(service);
    }

    // 租户端-获取服务详情
    @Override
    public Services getTenantDetail(Integer id) {
        return this.baseMapper.selectById(id);
    }
}