package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.SysUser;
import com.piano.management.exception.BusinessException;
import com.piano.management.service.SysUserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        SysUser user = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException("用户名不存在");
        }

        Integer status = user.getStatus();
        if (status != null && status == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (status != null && status == 2) {
            throw new BusinessException("账号正在审核中");
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        user.setPassword(null);
        request.getSession().setAttribute("loginUser", user);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        if (user == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        SysUser existing = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername()));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        sysUserService.save(user);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return Result.success();
    }

    @GetMapping("/info")
    public Result<SysUser> info(HttpServletRequest request) {
        SysUser user = (SysUser) request.getSession().getAttribute("loginUser");
        if (user == null) {
            throw new BusinessException(401, "未登录");
        }

        SysUser latest = sysUserService.getById(user.getId());
        if (latest == null) {
            request.getSession().invalidate();
            throw new BusinessException(401, "登录状态已失效，请重新登录");
        }
        latest.setPassword(null);
        return Result.success(latest);
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        SysUser user = (SysUser) request.getSession().getAttribute("loginUser");
        if (user == null) {
            throw new BusinessException(401, "未登录");
        }

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new BusinessException("原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException("新密码不能为空");
        }

        SysUser dbUser = sysUserService.getById(user.getId());
        if (dbUser == null) {
            request.getSession().invalidate();
            throw new BusinessException(401, "登录状态已失效，请重新登录");
        }

        if (!dbUser.getPassword().equals(DigestUtils.md5DigestAsHex(oldPassword.getBytes()))) {
            throw new BusinessException("原密码错误");
        }

        dbUser.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        sysUserService.updateById(dbUser);
        return Result.success();
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody SysUser user, HttpServletRequest request) {
        SysUser loginUser = (SysUser) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            throw new BusinessException(401, "未登录");
        }
        if (user == null) {
            throw new BusinessException("请求参数不能为空");
        }

        user.setId(loginUser.getId());
        user.setPassword(null);
        user.setRole(null);
        user.setUsername(null);
        sysUserService.updateById(user);

        SysUser updated = sysUserService.getById(loginUser.getId());
        if (updated == null) {
            request.getSession().invalidate();
            throw new BusinessException(401, "登录状态已失效，请重新登录");
        }
        updated.setPassword(null);
        request.getSession().setAttribute("loginUser", updated);
        return Result.success(updated);
    }
}
