package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.SysUser;
import com.piano.management.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) String name) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(SysUser::getName, name);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> p = sysUserService.page(new Page<>(pageNum, pageSize), wrapper);
        p.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(p);
    }

    @GetMapping("/teachers")
    public Result<?> teachers() {
        java.util.List<SysUser> teachers = sysUserService.list(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getRole, "TEACHER")
                        .eq(SysUser::getStatus, 1));
        teachers.forEach(u -> u.setPassword(null));
        return Result.success(teachers);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    public Result<?> save(@RequestBody SysUser user) {
        // 检查用户名唯一性
        SysUser existing = sysUserService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername()));
        if (existing != null) {
            throw new com.piano.management.exception.BusinessException("用户名已存在");
        }
        normalizeTeacherLimit(user);
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        sysUserService.save(user);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody SysUser user) {
        user.setPassword(null);
        normalizeTeacherLimit(user);
        sysUserService.updateById(user);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }

    @PutMapping("/resetPwd/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        sysUserService.updateById(user);
        return Result.success();
    }

    private void normalizeTeacherLimit(SysUser user) {
        if (user == null) {
            return;
        }
        if (!"TEACHER".equals(user.getRole())) {
            user.setTeacherStudentLimit(0);
            return;
        }
        if (user.getTeacherStudentLimit() == null || user.getTeacherStudentLimit() < 0) {
            user.setTeacherStudentLimit(0);
        }
    }
}
