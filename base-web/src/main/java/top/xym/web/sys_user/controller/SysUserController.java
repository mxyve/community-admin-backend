package top.xym.web.sys_user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.*;
import top.xym.jwt.JwtUtils;
import top.xym.result.ResultVo;
import top.xym.utils.ResultUtils;
import top.xym.web.sys_menu.entity.AssignTreeParm;
import top.xym.web.sys_menu.entity.AssignTreeVo;
import top.xym.web.sys_user.entity.*;
import top.xym.web.sys_user.service.SysUserService;
import top.xym.web.sys_user_role.entity.SysUserRole;
import top.xym.web.sys_user_role.service.SysUserRoleService;

import javax.imageio.ImageIO;



import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@RequestMapping("/api/sysUser")
@RestController
@AllArgsConstructor
public class SysUserController {
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final DefaultKaptcha defaultKaptcha;
    private final JwtUtils jwtUtils;

    //新增
    @PostMapping
    @Operation(summary = "新增用户")
    public ResultVo<?> add(@RequestBody SysUser sysUser) {
        sysUserService.saveUser(sysUser);
        return ResultUtils.success("新增成功！");
    }

    //编辑
    @PutMapping
    @Operation(summary = "编辑用户")
    public ResultVo<?> edit(@RequestBody SysUser sysUser) {
        sysUserService.editUser(sysUser);
        return ResultUtils.success("编辑成功！");
    }

    //删除
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户")
    public ResultVo<?> delete(@PathVariable("userId") Long userId) {
        sysUserService.deleteUser(userId);
        return ResultUtils.success("删除成功!");
    }

    // 列表
    @PostMapping("/list")
    @Operation(summary = "用户列表")
    public ResultVo<?> list(@RequestBody SysUserPage parm) {
        // 构造分页对象
        IPage<SysUser> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        // 构造查询条件
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getNickName())) {
            query.lambda().like(SysUser::getNickName, parm.getNickName());
        }
        if (StringUtils.isNotEmpty(parm.getPhone())) {
            query.lambda().like(SysUser::getPhone, parm.getPhone());
        }
        query.lambda().orderByDesc(SysUser::getCreateTime);
        // 查询列表
        IPage<SysUser> list = sysUserService.page(page, query);
        return ResultUtils.success("查询成功", list);
    }

    // 根据用户id查询用户的角色
    @GetMapping("/getRoleList")
    @Operation(summary = "根据用户id查询用户的角色")
    public ResultVo<?> getRoleList(Long userId) {
        QueryWrapper<SysUserRole> query = new QueryWrapper<>();
        query.lambda().eq(SysUserRole::getUserId, userId);
        List<SysUserRole> list = sysUserRoleService.list(query);
        // 角色id
        List<Long> roleList = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item -> {
                    roleList.add(item.getRoleId());
                });
        return ResultUtils.success("查询成功！", roleList);
    }

    // 重置密码
    @PostMapping("/resetPassword")
    @Operation(summary = "重置密码")
    public ResultVo<?> resetPassword(@RequestBody SysUser sysUser) {
        UpdateWrapper<SysUser> query = new UpdateWrapper<>();
        query.lambda().eq(SysUser::getUserId,sysUser.getUserId())
                .set(SysUser::getPassword,"666666");
        if (sysUserService.update(query)) {
            return ResultUtils.success("密码重置成功！");
        }
        return ResultUtils.error("密码重置失败！");
    }

    // 图片验证码
    @PostMapping("/getImage")
    @Operation(summary = "图片验证码")
    public ResultVo<?> imageCode(jakarta.servlet.http.HttpServletRequest request) {
        // 获取session
        jakarta.servlet.http.HttpSession session = request.getSession();
        // 生成验证码
        String text = defaultKaptcha.createText();
        // 存放到session
        session.setAttribute("code", text);
        // 生成图片，转换为base64
        BufferedImage bufferedImage = defaultKaptcha.createImage(text);
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            String base64 = Base64.encodeBase64String(outputStream.toByteArray());
            String captchaBase64 = "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
            return (ResultVo<?>) new ResultVo<>("生成成功", 200, captchaBase64);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 登录
    @PostMapping("/login")
    @Operation(summary = "登录")
    public ResultVo<?> login(HttpServletRequest request, @RequestBody LoginParm parm) {
        // 获取前端传递过来的code
        String code = parm.getCode();
        // 获取session
        HttpSession session = request.getSession();
        // 获取 session 里的code
        String code1 = (String) session.getAttribute("code");
        if (StringUtils.isEmpty(code1)) {
            return ResultUtils.error("验证码过期!");
        }
        // 判断传递进来的code和session里面的是否相等
        if (!code1.equals(code)) {
            return ResultUtils.error("验证码不正确!");
        }
        // 查询用户信息
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, parm.getUsername())
                .eq(SysUser::getPassword, parm.getPassword());
        SysUser one = sysUserService.getOne(query);
        if (one == null) {
            return ResultUtils.error("用户名或密码不正确!");
        }
        // 返回用户信息和token
        LoginVo vo = new LoginVo();
        vo.setUserId(one.getUserId());
        vo.setNickName(one.getNickName());
        //生成token
        Map<String,String> map = new HashMap<>();
        map.put("userId",Long.toString(one.getUserId()));
        String token = jwtUtils.generateToken(map);
        vo.setToken(token);
        return ResultUtils.success("登录成功!", vo);
    }

    // 查询菜单树
    @PostMapping("/tree")
    @Operation(summary = "查询菜单树")
    public ResultVo<?> getAssignTree(@RequestBody AssignTreeParm parm) {
        AssignTreeVo assignTree = sysUserService.getAssignTree(parm);
        return ResultUtils.success("查询成功", assignTree);
    }

    // 修改密码
    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public ResultVo<?> updatePassword(@RequestBody UpdatePasswordParm parm) {
        SysUser user = sysUserService.getById(parm.getUserId());
        if (!parm.getOldPassword().equals(user.getPassword())){
            return ResultUtils.error("原密码不正确！");
        }
        // 更新条件
        UpdateWrapper<SysUser> query = new UpdateWrapper<>();
        query.lambda().set(SysUser::getPassword,parm.getPassword())
                .eq(SysUser::getUserId,parm.getUserId());
        if(sysUserService.update(query)) {
            return ResultUtils.success("密码修改成功!");
        }
        return ResultUtils.error("密码修改失败！");

    }
}
