package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.ActivityRegistration;
import com.piano.management.entity.Course;
import com.piano.management.entity.CourseBooking;
import com.piano.management.entity.OrderInfo;
import com.piano.management.entity.SysUser;
import com.piano.management.mapper.ActivityRegistrationMapper;
import com.piano.management.mapper.CourseBookingMapper;
import com.piano.management.mapper.OrderInfoMapper;
import com.piano.management.service.CourseBookingService;
import com.piano.management.service.CourseService;
import com.piano.management.service.OrderInfoService;
import com.piano.management.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private ActivityRegistrationMapper activityRegistrationMapper;
    @Resource
    private CourseBookingMapper courseBookingMapper;
    @Resource
    private CourseBookingService courseBookingService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CourseService courseService;

    @GetMapping("/overview")
    public Result<?> overview() {
        Map<String, Object> paid = orderInfoMapper.selectPaidOverview();
        if (paid == null) {
            paid = new HashMap<>();
            paid.put("total_amount", 0);
            paid.put("paid_order_count", 0);
        }
        Map<String, Object> m = new HashMap<>(paid);
        m.put("studentCount", sysUserService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "STUDENT")));
        m.put("teacherCount", sysUserService.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "TEACHER")));
        m.put("pendingBookings", courseBookingService.count(
                new LambdaQueryWrapper<CourseBooking>().eq(CourseBooking::getStatus, 0)));
        m.put("activityRegistrations", activityRegistrationMapper.selectCount(new LambdaQueryWrapper<ActivityRegistration>()));
        return Result.success(m);
    }

    @GetMapping("/bookingByStatus")
    public Result<?> bookingByStatus() {
        return Result.success(courseBookingMapper.selectBookingCountByStatus());
    }

    @GetMapping("/topProducts")
    public Result<?> topProducts() {
        return Result.success(orderInfoMapper.selectTopProductIncome());
    }

    @GetMapping("/monthlyIncome")
    public Result<?> monthlyIncome() {
        String startDate = LocalDateTime.now().minusMonths(12).toString().substring(0, 10);
        return Result.success(orderInfoMapper.selectMonthlyIncome(startDate));
    }

    @GetMapping("/incomeByType")
    public Result<?> incomeByType() {
        return Result.success(orderInfoMapper.selectIncomeByType());
    }

    @GetMapping("/activityParticipation")
    public Result<?> activityParticipation() {
        return Result.success(activityRegistrationMapper.selectParticipationByActivity());
    }

    @GetMapping("/teacherIncome")
    public Result<?> teacherIncome(@RequestParam Long teacherId,
                                   @RequestParam(required = false) String period) {
        List<Long> courseIds = courseService.list(new LambdaQueryWrapper<Course>()
                        .eq(Course::getTeacherId, teacherId))
                .stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        if (courseIds.isEmpty()) {
            return Result.success(java.util.Collections.emptyList());
        }

        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOrderType, "COURSE")
                .eq(OrderInfo::getStatus, 1)
                .in(OrderInfo::getProductId, courseIds);
        if ("week".equals(period)) {
            wrapper.ge(OrderInfo::getPayTime, LocalDateTime.now().minusWeeks(1));
        } else if ("month".equals(period)) {
            wrapper.ge(OrderInfo::getPayTime, LocalDateTime.now().minusMonths(1));
        } else if ("year".equals(period)) {
            wrapper.ge(OrderInfo::getPayTime, LocalDateTime.now().minusYears(1));
        }
        wrapper.orderByDesc(OrderInfo::getPayTime);
        return Result.success(orderInfoService.list(wrapper));
    }
}
