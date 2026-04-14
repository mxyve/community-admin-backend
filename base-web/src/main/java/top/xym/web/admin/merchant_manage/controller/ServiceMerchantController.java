package top.xym.web.admin.merchant_manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.admin.merchant_manage.entity.MerchantPageParm;
import top.xym.web.admin.merchant_manage.entity.ServiceMerchant;
import top.xym.web.admin.merchant_manage.entity.dto.StatusUpdateDto;
import top.xym.web.admin.merchant_manage.service.ServiceMerchantService;
import top.xym.web.rag.utils.AliOssUtil;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/serviceMerchant")
@AllArgsConstructor
@Tag(name = "租户管理模块")
public class ServiceMerchantController {

    private final ServiceMerchantService serviceMerchantService;
    private final AliOssUtil aliOssUtil;

    // 商家入驻
    @PostMapping("/apply")
    @Operation(summary = "商家入驻申请")
    public ResultVo<?> apply(@RequestBody ServiceMerchant merchant) {
        serviceMerchantService.applyMerchant(merchant);
        return ResultUtils.success("入驻申请提交成功，请等待审核！");
    }

    /**
     * 上传法人身份证图片
     */
    @PostMapping("/idCard")
    @Operation(summary = "上传法人身份证图片")
    public ResultVo<?> uploadIdCard(@RequestParam("file") MultipartFile file) throws IOException {
        // 原始文件名
        String originalFileName = file.getOriginalFilename();
        // 生成唯一文件名
        String fileName = "community/merchant/idCard/" + aliOssUtil.generateUniqueFileName(originalFileName);
        // 上传OSS
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        // 返回URL
        return ResultUtils.success(url);
    }

    /**
     * 上传营业执照图片
     */
    @PostMapping("/businessLicense")
    @Operation(summary = "上传营业执照图片")
    public ResultVo<?> uploadBusinessLicense(@RequestParam("file") MultipartFile file) throws IOException {
        // 原始文件名
        String originalFileName = file.getOriginalFilename();
        // 生成唯一文件名
        String fileName = "community/merchant/license/" + aliOssUtil.generateUniqueFileName(originalFileName);
        // 上传OSS
        String url = aliOssUtil.upload(file.getBytes(), fileName);
        // 返回URL
        return ResultUtils.success(url);
    }

    // 租户管理-列表
    @PostMapping("/list")
    @Operation(summary = "租户管理-列表")
    public ResultVo<?> list(@RequestBody MerchantPageParm parm) {
        IPage<ServiceMerchant> list = serviceMerchantService.getMerchantList(parm);
        return ResultUtils.success("查询成功", list);
    }

    // 编辑
    @PutMapping
    @Operation(summary = "编辑租户信息")
    public ResultVo<?> edit(@RequestBody ServiceMerchant merchant) {
        serviceMerchantService.updateById(merchant);
        return ResultUtils.success("编辑成功");
    }

    // 删除
    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户信息")
    public ResultVo<?> delete(@PathVariable Long id) {
        serviceMerchantService.removeById(id);
        return ResultUtils.success("删除成功");
    }

    // 更新租户状态接口
    @PutMapping("/status")
    @Operation(summary = "更新租户审核状态")
    public ResultVo<?> updateStatus(@RequestBody StatusUpdateDto request) {
        serviceMerchantService.updateStatus(request.getId(), request.getApplyStatus(), request.getApplyDesc());
        return ResultUtils.success("更新成功");
    }

    @PostMapping("/createPay")
    @Operation(summary = "创建支付宝沙箱支付订单")
    public ResultVo<?> createPay(@RequestBody Map<String, String> map) {
        String packageType = map.get("packageType");
        String orderNo = map.get("orderNo");
        return serviceMerchantService.createAlipayQrCode(packageType, orderNo);
    }

    @PostMapping("/payNotify")
    @Operation(summary = "支付宝支付成功回调")
    public String payNotify(HttpServletRequest request) {
        // 支付成功后支付宝自动调用这里
        return serviceMerchantService.payNotify(request);
    }

    @GetMapping("/payStatus/{orderNo}")
    @Operation(summary = "查询支付状态")
    public ResultVo<?> getPayStatus(@PathVariable String orderNo) {
        return serviceMerchantService.getPayStatus(orderNo);
    }
}
