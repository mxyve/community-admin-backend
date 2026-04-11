package top.xym.web.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.utils.SecurityUtils;
import top.xym.web.rag.utils.AliOssUtil;
import top.xym.web.shop.service.entity.ServicePageParm;
import top.xym.web.shop.service.entity.Services;
import top.xym.web.shop.service.service.ShopServiceService;
import top.xym.web.sys_user.entity.SysUser;
import top.xym.web.sys_user.service.SysUserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/tenant/service")
@AllArgsConstructor
@Slf4j
@Tag(name = "shop服务管理模块")
public class ShopServiceController {

    private final ShopServiceService servicesService;
    private final SysUserService sysUserService;

    private final AliOssUtil aliOssUtil;

    private Integer getCurrentMerchantId() {
        Long userId = SecurityUtils.getCurrentUserId();
        System.out.println("登录的用户id：" + userId);
        SysUser user = sysUserService.getById(userId);
        if (user == null || user.getTenantId() == null) {
            throw new RuntimeException("当前用户不是租户管理员");
        }
        return user.getTenantId().intValue();
    }

    // 租户端-分页查询服务列表
    @PostMapping("/list")
    @Operation(summary = "租户端-查询服务列表")
    public ResultVo<?> list(@RequestBody ServicePageParm parm) {
        IPage<Services> page = servicesService.getTenantServiceList(parm, getCurrentMerchantId());
        return ResultUtils.success("查询成功", page);
    }

    /**
     * 服务封面图上传 OSS
     */
    @PostMapping("/cover/upload")
    @Operation(summary = "上传服务封面图")
    public ResultVo<?> uploadServiceCover(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = "community/service/cover/" + aliOssUtil.generateUniqueFileName(originalFileName);
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        return ResultUtils.success(url);
    }

    /**
     * 服务轮播图上传 OSS
     */
    @PostMapping("/banner/upload")
    @Operation(summary = "上传服务轮播图")
    public ResultVo<?> uploadServiceBanner(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = "community/service/banner/" + aliOssUtil.generateUniqueFileName(originalFileName);
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        return ResultUtils.success(url);
    }

    // 租户端-新增服务
    @PostMapping
    @Operation(summary = "租户端-新增服务")
    public ResultVo<?> create(@RequestBody Services service) {
        service.setMerchantId(getCurrentMerchantId());
        servicesService.tenantCreate(service);
        return ResultUtils.success("服务创建成功，等待平台审核");
    }

    // 租户端-修改服务
    @PutMapping
    @Operation(summary = "租户端-修改服务")
    public ResultVo<?> update(@RequestBody Services service) {
        servicesService.tenantUpdate(service);
        return ResultUtils.success("服务修改成功，等待平台重新审核");
    }

    // 租户端-删除服务
    @DeleteMapping("/{id}")
    @Operation(summary = "租户端-删除服务")
    public ResultVo<?> delete(@PathVariable Integer id) {
        servicesService.tenantDelete(id);
        return ResultUtils.success("服务删除成功");
    }

    // 租户端-下架服务
    @PutMapping("/offline/{id}")
    @Operation(summary = "租户端-下架服务")
    public ResultVo<?> offline(@PathVariable Integer id) {
        servicesService.tenantOffline(id);
        return ResultUtils.success("服务已下架");
    }

    // 租户端-申请上架
    @PutMapping("/apply/{id}")
    @Operation(summary = "租户端-申请上架")
    public ResultVo<?> applyOnline(@PathVariable Integer id) {
        servicesService.tenantApplyOnline(id);
        return ResultUtils.success("已提交上架申请，等待审核");
    }

    // 租户端-获取服务详情
    @GetMapping("/{id}")
    @Operation(summary = "租户端-获取服务详情")
    public ResultVo<?> detail(@PathVariable Integer id) {
        Services service = servicesService.getTenantDetail(id);
        return ResultUtils.success("查询成功", service);
    }
}
