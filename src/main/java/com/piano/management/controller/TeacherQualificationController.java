package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.TeacherQualification;
import com.piano.management.service.TeacherQualificationService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/qualification")
public class TeacherQualificationController {
    @Resource
    private TeacherQualificationService qualificationService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long teacherId) {
        LambdaQueryWrapper<TeacherQualification> wrapper = new LambdaQueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq(TeacherQualification::getTeacherId, teacherId);
        }
        wrapper.orderByDesc(TeacherQualification::getCreateTime);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TeacherQualification> p =
                qualificationService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        return Result.success(p);
    }

    @PostMapping
    public Result<?> save(@RequestBody TeacherQualification q) { qualificationService.save(q); return Result.success(); }

    @PutMapping
    public Result<?> update(@RequestBody TeacherQualification q) { qualificationService.updateById(q); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { qualificationService.removeById(id); return Result.success(); }
}
