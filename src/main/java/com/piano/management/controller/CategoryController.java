package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Category;
import com.piano.management.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Category> p = categoryService.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getId));
        return Result.success(p);
    }

    @GetMapping("/all")
    public Result<?> all() {
        return Result.success(categoryService.list(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getId)));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Category entity) {
        categoryService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Category entity) {
        categoryService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}
