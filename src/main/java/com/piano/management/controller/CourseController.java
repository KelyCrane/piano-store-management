package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Category;
import com.piano.management.entity.Course;
import com.piano.management.entity.SysUser;
import com.piano.management.service.CategoryService;
import com.piano.management.service.CourseService;
import com.piano.management.service.SysUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private SysUserService sysUserService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) Long teacherId,
                          @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like(Course::getName, name);
        if (teacherId != null) wrapper.eq(Course::getTeacherId, teacherId);
        if (status != null) wrapper.eq(Course::getStatus, status);
        wrapper.orderByDesc(Course::getCreateTime);
        Page<Course> p = courseService.page(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, String> catMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        Set<Long> teacherIds = p.getRecords().stream()
                .map(Course::getTeacherId).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> teacherMap = new java.util.HashMap<>();
        if (!teacherIds.isEmpty()) {
            sysUserService.listByIds(teacherIds).forEach(u -> teacherMap.put(u.getId(), u.getName()));
        }
        p.getRecords().forEach(c -> {
            c.setCategoryName(catMap.get(c.getCategoryId()));
            c.setTeacherName(teacherMap.get(c.getTeacherId()));
        });
        return Result.success(p);
    }

    @GetMapping("/all")
    public Result<?> all() { return Result.success(courseService.list()); }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Course c = courseService.getById(id);
        if (c == null) {
            return Result.error(404, "课程不存在");
        }
        if (c.getCategoryId() != null) {
            Category cat = categoryService.getById(c.getCategoryId());
            if (cat != null) {
                c.setCategoryName(cat.getName());
            }
        }
        if (c.getTeacherId() != null) {
            SysUser u = sysUserService.getById(c.getTeacherId());
            if (u != null) {
                c.setTeacherName(u.getName());
            }
        }
        return Result.success(c);
    }

    @PostMapping
    public Result<?> save(@RequestBody Course course) { courseService.save(course); return Result.success(); }

    @PutMapping
    public Result<?> update(@RequestBody Course course) { courseService.updateById(course); return Result.success(); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { courseService.removeById(id); return Result.success(); }
}
