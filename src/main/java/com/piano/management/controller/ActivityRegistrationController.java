package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.entity.Activity;
import com.piano.management.entity.ActivityRegistration;
import com.piano.management.entity.OrderInfo;
import com.piano.management.exception.BusinessException;
import com.piano.management.mapper.ActivityRegistrationMapper;
import com.piano.management.service.ActivityService;
import com.piano.management.service.ActivityRegistrationService;
import com.piano.management.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
public class ActivityRegistrationController {
    @Resource
    private ActivityRegistrationService registrationService;
    @Resource
    private ActivityRegistrationMapper registrationMapper;
    @Resource
    private ActivityService activityService;
    @Resource
    private OrderInfoService orderService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long activityId,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) Long teacherId,
                          @RequestParam(required = false) String activityType,
                          @RequestParam(required = false) Integer status) {
        ActivityRegistration param = new ActivityRegistration();
        param.setActivityId(activityId);
        param.setStudentId(studentId);
        param.setTeacherId(teacherId);
        param.setActivityType(activityType);
        param.setStatus(status);
        List<ActivityRegistration> list = registrationMapper.selectPageWithNames(param);
        int total = list.size();
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        java.util.Map<String, Object> page = new java.util.HashMap<>();
        page.put("records", from < total ? list.subList(from, to) : list.subList(0, 0));
        page.put("total", total);
        return Result.success(page);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody ActivityRegistration reg) {
        Activity activity = validateCreate(reg);
        if (reg.getStatus() == null) reg.setStatus(0);
        BigDecimal fee = activity.getFee() == null ? BigDecimal.ZERO : activity.getFee();
        reg.setFeePaid(fee.compareTo(BigDecimal.ZERO) > 0 ? 0 : 1);
        registrationService.save(reg);
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            createActivityOrder(reg, activity, fee);
        }
        return Result.success(reg);
    }

    @PutMapping
    public Result<?> update(@RequestBody ActivityRegistration reg) {
        if (reg.getId() == null) {
            throw new BusinessException("报名记录ID不能为空");
        }
        if (hasResultFields(reg)) {
            ActivityRegistration db = registrationService.getById(reg.getId());
            if (db == null) {
                throw new BusinessException("报名记录不存在");
            }
            Integer targetStatus = reg.getStatus() != null ? reg.getStatus() : db.getStatus();
            if (targetStatus == null || targetStatus != 1) {
                throw new BusinessException("仅已通过报名可录入成绩");
            }
            Integer targetFeePaid = reg.getFeePaid() != null ? reg.getFeePaid() : db.getFeePaid();
            if (targetFeePaid == null || targetFeePaid != 1) {
                throw new BusinessException("报名未缴费，不能录入成绩");
            }
        }
        registrationService.updateById(reg);
        return Result.success();
    }

    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam Integer status) {
        ActivityRegistration reg = new ActivityRegistration();
        reg.setId(id);
        reg.setStatus(status);
        registrationService.updateById(reg);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> cancel(@PathVariable Long id) {
        ActivityRegistration db = registrationService.getById(id);
        if (db == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (db.getStatus() != null && db.getStatus() == 3) {
            return Result.success();
        }
        Activity activity = activityService.getById(db.getActivityId());
        if (activity != null) {
            Integer runtimeStatus = ActivityController.resolveRuntimeStatus(activity);
            if (runtimeStatus != null && runtimeStatus == 2) {
                throw new BusinessException("活动已结束，不能取消报名");
            }
        }
        if (hasResultFields(db)) {
            throw new BusinessException("已录入成绩的报名不能取消");
        }

        OrderInfo order = findActivityOrder(db);
        if (order != null && (order.getStatus() == null || order.getStatus() != 2)) {
            OrderInfo updateOrder = new OrderInfo();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(2);
            updateOrder.setCompleteTime(LocalDateTime.now());
            updateOrder.setRemark(appendRemark(order.getRemark(), order.getStatus() != null && order.getStatus() == 1
                    ? "活动取消，报名费已退款"
                    : "活动取消，缴费订单已取消"));
            orderService.updateById(updateOrder);
        }

        ActivityRegistration update = new ActivityRegistration();
        update.setId(id);
        update.setStatus(3);
        update.setFeePaid(0);
        registrationService.updateById(update);
        return Result.success();
    }

    @PutMapping("/result/{id}")
    public Result<?> setResult(@PathVariable Long id, @RequestBody ActivityRegistration result) {
        ActivityRegistration db = registrationService.getById(id);
        if (db == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (db.getStatus() == null || db.getStatus() != 1) {
            throw new BusinessException("仅已通过报名可录入成绩");
        }
        if (db.getFeePaid() == null || db.getFeePaid() != 1) {
            throw new BusinessException("报名未缴费，不能录入成绩");
        }
        result.setId(id);
        registrationService.updateById(result);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { registrationService.removeById(id); return Result.success(); }

    private boolean hasResultFields(ActivityRegistration reg) {
        return reg.getPassed() != null
                || reg.getRank() != null
                || reg.getAwardLevel() != null;
    }

    private Activity validateCreate(ActivityRegistration reg) {
        if (reg == null || reg.getActivityId() == null) {
            throw new BusinessException("活动信息不能为空");
        }
        if (reg.getStudentId() == null) {
            throw new BusinessException("学生信息不能为空");
        }
        Activity activity = activityService.getById(reg.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        Integer runtimeStatus = ActivityController.resolveRuntimeStatus(activity);
        if (runtimeStatus != null && runtimeStatus == 2) {
            throw new BusinessException("活动已结束，不能报名");
        }

        long activeCount = registrationService.count(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, reg.getActivityId())
                .eq(ActivityRegistration::getStudentId, reg.getStudentId())
                .notIn(ActivityRegistration::getStatus, 2, 3));
        if (activeCount > 0) {
            throw new BusinessException("已报名该活动，请勿重复提交");
        }

        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            long acceptedCount = registrationService.count(new LambdaQueryWrapper<ActivityRegistration>()
                    .eq(ActivityRegistration::getActivityId, reg.getActivityId())
                    .notIn(ActivityRegistration::getStatus, 2, 3));
            if (acceptedCount >= activity.getMaxParticipants()) {
                throw new BusinessException("活动名额已满");
            }
        }
        return activity;
    }

    private void createActivityOrder(ActivityRegistration reg, Activity activity, BigDecimal fee) {
        OrderInfo order = new OrderInfo();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        order.setStudentId(reg.getStudentId());
        order.setOrderType("ACTIVITY");
        order.setProductId(activity.getId());
        order.setQuantity(1);
        order.setOriginalAmount(fee);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(fee);
        order.setStatus(0);
        order.setRemark("活动报名缴费：" + activityDisplayName(activity));
        orderService.save(order);
    }

    private OrderInfo findActivityOrder(ActivityRegistration reg) {
        if (reg == null || reg.getStudentId() == null || reg.getActivityId() == null) {
            return null;
        }
        return orderService.getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getStudentId, reg.getStudentId())
                .eq(OrderInfo::getOrderType, "ACTIVITY")
                .eq(OrderInfo::getProductId, reg.getActivityId())
                .in(OrderInfo::getStatus, 0, 1)
                .orderByDesc(OrderInfo::getCreateTime)
                .last("limit 1"), false);
    }

    private String appendRemark(String oldRemark, String appendText) {
        if (oldRemark == null || oldRemark.trim().isEmpty()) {
            return appendText;
        }
        return oldRemark + "；" + appendText;
    }

    private String activityDisplayName(Activity activity) {
        if (activity.getTitle() != null && activity.getTitle().trim().length() > 0) {
            return activity.getTitle().trim();
        }
        return activity.getName();
    }
}
