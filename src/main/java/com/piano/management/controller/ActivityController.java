package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.Activity;
import com.piano.management.exception.BusinessException;
import com.piano.management.service.ActivityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String type) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(type)) wrapper.eq(Activity::getType, type);
        wrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> page = activityService.page(new Page<>(pageNum, pageSize), wrapper);
        fillRuntimeStatus(page.getRecords());
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<?> all() {
        List<Activity> list = activityService.list();
        fillRuntimeStatus(list);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        fillRuntimeStatus(activity);
        return Result.success(activity);
    }

    @PostMapping
    public Result<?> save(@RequestBody Activity activity) {
        normalizeActivity(activity);
        activityService.save(activity);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Activity activity) {
        normalizeActivity(activity);
        activityService.updateById(activity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { activityService.removeById(id); return Result.success(); }

    private void normalizeActivity(Activity activity) {
        if (activity == null) return;
        if (activity.getStartTime() != null && activity.getEndTime() != null
                && activity.getEndTime().isBefore(activity.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        if (StringUtils.isBlank(activity.getTitle())) {
            activity.setTitle(StringUtils.defaultIfBlank(activity.getName(), "未命名活动"));
        }
        if (StringUtils.isBlank(activity.getName())) {
            activity.setName(activity.getTitle());
        }
        if (activity.getStatus() == null) {
            activity.setStatus(0);
        }
    }

    private void fillRuntimeStatus(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return;
        }
        for (Activity activity : activities) {
            fillRuntimeStatus(activity);
        }
    }

    private void fillRuntimeStatus(Activity activity) {
        Integer runtimeStatus = resolveRuntimeStatus(activity);
        if (runtimeStatus != null) {
            activity.setStatus(runtimeStatus);
        }
    }

    static Integer resolveRuntimeStatus(Activity activity) {
        if (activity == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (activity.getEndTime() != null && now.isAfter(activity.getEndTime())) {
            return 2;
        }
        if (activity.getStartTime() != null && now.isBefore(activity.getStartTime())) {
            return 0;
        }
        if (activity.getStartTime() != null && activity.getEndTime() != null) {
            return 1;
        }
        return activity.getStatus();
    }
}
