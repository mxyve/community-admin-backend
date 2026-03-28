package top.xym.web.admin.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.web.admin.service.entity.AdminServiceParm;
import top.xym.web.admin.service.entity.ServiceAuditParm;
import top.xym.web.admin.service.entity.Services;
import top.xym.web.admin.service.mapper.ServiceItemMapper;
import top.xym.web.admin.service.service.ServiceItemService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ServiceItemServiceImpl
        extends ServiceImpl<ServiceItemMapper, Services>
        implements ServiceItemService {

    private final ServiceItemMapper adminShopServiceMapper;

    @Override
    public long countByCategoryId(Integer categoryId) {
        LambdaQueryWrapper<Services> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Services::getCategoryId, categoryId)
                .eq(Services::getDeleted, 0);
        return this.count(wrapper);
    }

    @Override
    public IPage<Services> getAdminServiceList(AdminServiceParm parm) {
        IPage<Services> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        return adminShopServiceMapper.selectAdminServicePage(page, parm);
    }

    // 审核通过
    @Transactional
    @Override
    public void auditPass(ServiceAuditParm parm) {
        Services service = getById(parm.getId());
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        // 审核通过
        service.setAuditStatus(1);
        service.setStatus(1);
        service.setAuditTime(LocalDateTime.now());
        updateById(service);
    }

    // 审核不通过
    @Transactional
    @Override
    public void auditReject(ServiceAuditParm parm) {
        Services service = getById(parm.getId());
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        service.setAuditStatus(0);
        service.setAuditReason(parm.getAuditReason());
        service.setAuditTime(LocalDateTime.now());
        updateById(service);
    }

    // 重新审核
    @Override
    public void reAudit(Integer id) {
        Services service = getById(id);
        if (service == null) {
            throw new RuntimeException("服务不存在");
        }
        // 重新审核 → 恢复成【待审核】
        service.setStatus(2);
        service.setAuditStatus(2);
        updateById(service);
    }

    // 详情
    @Override
    public Services getAdminDetail(Integer id) {
        return getById(id);
    }
}
