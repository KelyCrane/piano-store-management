package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Classroom;
import com.piano.management.service.ClassroomService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/classroom")
public class ClassroomController {

    @Resource
    private ClassroomService classroomService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<Classroom>()
                .orderByDesc(Classroom::getCreateTime);
        if (status != null) {
            wrapper.eq(Classroom::getStatus, status);
        }
        Page<Classroom> p = classroomService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(p);
    }

    @GetMapping("/all")
    public Result<?> all(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Classroom> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Classroom::getStatus, status);
        }
        return Result.success(classroomService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(classroomService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Classroom entity) {
        classroomService.save(entity);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Classroom entity) {
        classroomService.updateById(entity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        classroomService.removeById(id);
        return Result.success();
    }
}
