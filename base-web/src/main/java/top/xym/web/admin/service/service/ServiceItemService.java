package top.xym.web.admin.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.xym.web.admin.service.entity.AdminServiceParm;
import top.xym.web.admin.service.entity.ServiceAuditParm;
import top.xym.web.admin.service.entity.Services;

public interface ServiceItemService extends IService<Services> {

    /**
     * 根据分类ID统计服务项目数量
     * @param categoryId 分类ID
     * @return 数量
     */
    long countByCategoryId(Integer categoryId);

    // 超管分页查询
    IPage<Services> getAdminServiceList(AdminServiceParm parm);

    // 审核通过
    void auditPass(ServiceAuditParm parm);

    // 审核不通过
    void auditReject(ServiceAuditParm parm);

    // 重新审核
    void reAudit(Integer id);

    // 超管查看详情（带商家）
    Services getAdminDetail(Integer id);
}
