package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Banner;
import com.piano.management.service.BannerService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Resource
    private BannerService bannerService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Banner::getStatus, status);
        wrapper.orderByAsc(Banner::getSort).orderByDesc(Banner::getCreateTime);
        Page<Banner> p = bannerService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(p);
    }

    @GetMapping("/all")
    public Result<?> all() {
        return Result.success(bannerService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(bannerService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Banner entity) {
        bannerService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Banner entity) {
        bannerService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }
}