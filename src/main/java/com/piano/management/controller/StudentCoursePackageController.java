package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.mapper.StudentCoursePackageMapper;
import com.piano.management.service.PackageAvailabilityService;
import com.piano.management.service.StudentCoursePackageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/studentPackage")
public class StudentCoursePackageController {
    @Resource
    private StudentCoursePackageService packageService;
    @Resource
    private StudentCoursePackageMapper packageMapper;
    @Resource
    private PackageAvailabilityService packageAvailabilityService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) Long teacherId,
                          @RequestParam(required = false) Long courseId,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String name) {
        StudentCoursePackage param = new StudentCoursePackage();
        param.setStudentId(studentId);
        param.setTeacherId(teacherId);
        param.setCourseId(courseId);
        param.setStatus(status);
        param.setName(name);
        List<StudentCoursePackage> list = packageMapper.selectPageWithNames(param);
        fillUsageSummary(list);
        int total = list.size();
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<StudentCoursePackage> records = from < total ? list.subList(from, to) : list.subList(0, 0);
        Map<String, Object> page = new HashMap<>();
        page.put("records", records);
        page.put("total", total);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        StudentCoursePackage pkg = packageService.getById(id);
        fillUsageSummary(java.util.Collections.singletonList(pkg));
        return Result.success(pkg);
    }

    @PostMapping
    public Result<?> save(@RequestBody StudentCoursePackage pkg) {
        if (pkg.getTotalHours() != null && pkg.getTotalHours() > 0
                && (pkg.getRemainingHours() == null || pkg.getRemainingHours() < 0)) {
            pkg.setRemainingHours(pkg.getTotalHours());
        }
        if (pkg.getStatus() == null) {
            pkg.setStatus(1);
        }
        packageService.save(pkg);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody StudentCoursePackage pkg) {
        packageService.updateById(pkg);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        packageService.removeById(id);
        return Result.success();
    }

    private void fillUsageSummary(List<StudentCoursePackage> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, Integer> availableMap = packageAvailabilityService.calculateAvailableHours(list);
        for (StudentCoursePackage pkg : list) {
            if (pkg == null || pkg.getId() == null) {
                continue;
            }
            int remainingHours = pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours();
            int availableHours = availableMap.getOrDefault(pkg.getId(), remainingHours);
            pkg.setAvailableHours(availableHours);
            pkg.setReservedHours(Math.max(remainingHours - availableHours, 0));
        }
    }
}
