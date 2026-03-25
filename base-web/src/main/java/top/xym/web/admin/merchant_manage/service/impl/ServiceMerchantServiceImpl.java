package top.xym.web.admin.merchant_manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xym.web.admin.merchant_manage.entity.MerchantPageParm;
import top.xym.web.admin.merchant_manage.entity.ServiceMerchant;
import top.xym.web.admin.merchant_manage.mapper.ServiceMerchantMapper;
import top.xym.web.admin.merchant_manage.service.ServiceMerchantService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import java.util.Date;

@Service
@AllArgsConstructor
public class ServiceMerchantServiceImpl extends ServiceImpl<ServiceMerchantMapper, ServiceMerchant> implements ServiceMerchantService {

    private final SysUserService sysUserService;

    // 商家入驻申请
    @Transactional
    @Override
    public void applyMerchant(ServiceMerchant merchant) {
        // 1.插入商家信息，状态默认待审核 0
        merchant.setApplyStatus(0);
        merchant.setApplyTime(new Date());
        merchant.setDeleted(0);
        this.baseMapper.insert(merchant);

//        // 2.自动创建租户管理员账号（sys_user）
//        SysUser user = new SysUser();
//        user.setUsername(merchant.getContactPhone());
//        user.setPassword("123456");
//        user.setPhone(merchant.getContactPhone());
//        user.setNickName(merchant.getContactPerson());
//        user.setEmail(merchant.getEmail());
//        // 绑定租户ID
//        user.setTenantId(merchant.getId());
//        sysUserService.save(user);
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

}

