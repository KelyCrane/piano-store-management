package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.entity.OperationLog;
import com.piano.management.service.OperationLogService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/log")
public class OperationLogController {
    @Resource
    private OperationLogService logService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String module) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(OperationLog::getUsername, username.trim());
        }
        if (module != null && !module.trim().isEmpty()) {
            wrapper.like(OperationLog::getModule, module.trim());
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return Result.success(logService.page(new Page<>(pageNum, pageSize),
                wrapper));
    }
}
