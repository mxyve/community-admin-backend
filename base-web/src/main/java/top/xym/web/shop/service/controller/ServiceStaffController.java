package top.xym.web.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.utils.SecurityUtils;
import top.xym.web.shop.service.entity.ServiceStaff;
import top.xym.web.shop.service.entity.StaffPageParm;
import top.xym.web.shop.service.service.ServiceStaffService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

@RestController
@RequestMapping("/api/tenant/staff")
@AllArgsConstructor
@Slf4j
public class ServiceStaffController {

    private final ServiceStaffService staffService;
    private final SysUserService sysUserService;

    // 完全照搬你的写法 → 获取当前租户ID
    private Integer getCurrentMerchantId() {
        Long userId = SecurityUtils.getCurrentUserId();
        System.out.println("登录的用户id：" + userId);
        SysUser user = sysUserService.getById(userId);
        if (user == null || user.getTenantId() == null) {
            throw new RuntimeException("当前用户不是租户管理员");
        }
        return user.getTenantId().intValue();
    }

    // 租户端-分页查询服务人员列表
    @PostMapping("/list")
    @Operation(summary = "租户端-查询服务人员列表")
    public ResultVo<?> list(@RequestBody StaffPageParm parm) {
        IPage<ServiceStaff> page = staffService.getTenantStaffList(parm, getCurrentMerchantId());
        return ResultUtils.success("查询成功", page);
    }

    // 租户端-新增服务人员
    @PostMapping
    @Operation(summary = "租户端-新增服务人员")
    public ResultVo<?> create(@RequestBody ServiceStaff staff) {
        staff.setMerchantId(getCurrentMerchantId());
        staffService.tenantCreate(staff);
        return ResultUtils.success("服务人员创建成功");
    }

    // 租户端-修改服务人员
    @PutMapping
    @Operation(summary = "租户端-修改服务人员")
    public ResultVo<?> update(@RequestBody ServiceStaff staff) {
        staffService.tenantUpdate(staff);
        return ResultUtils.success("服务人员修改成功");
    }

    // 租户端-删除服务人员
    @DeleteMapping("/{id}")
    @Operation(summary = "租户端-删除服务人员")
    public ResultVo<?> delete(@PathVariable Integer id) {
        staffService.tenantDelete(id);
        return ResultUtils.success("服务人员删除成功");
    }

    // 租户端-获取服务人员详情
    @GetMapping("/{id}")
    @Operation(summary = "租户端-获取服务人员详情")
    public ResultVo<?> detail(@PathVariable Integer id) {
        ServiceStaff staff = staffService.getTenantDetail(id);
        return ResultUtils.success("查询成功", staff);
    }
}