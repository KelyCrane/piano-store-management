package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.entity.ActivityRegistration;
import com.piano.management.entity.Course;
import com.piano.management.entity.OrderInfo;
import com.piano.management.entity.Product;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.exception.BusinessException;
import com.piano.management.mapper.OrderInfoMapper;
import com.piano.management.service.ActivityRegistrationService;
import com.piano.management.service.CourseService;
import com.piano.management.service.OrderInfoService;
import com.piano.management.service.ProductService;
import com.piano.management.service.StudentCoursePackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderInfoController {
    @Resource
    private OrderInfoService orderService;
    @Resource
    private OrderInfoMapper orderMapper;
    @Resource
    private ProductService productService;
    @Resource
    private CourseService courseService;
    @Resource
    private StudentCoursePackageService packageService;
    @Resource
    private ActivityRegistrationService registrationService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) String orderType,
                          @RequestParam(required = false) Integer status) {
        OrderInfo param = new OrderInfo();
        param.setStudentId(studentId);
        param.setOrderType(orderType);
        param.setStatus(status);
        List<OrderInfo> list = orderMapper.selectPageWithNames(param);
        int total = list.size();
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        java.util.Map<String, Object> page = new java.util.HashMap<>();
        page.put("records", from < total ? list.subList(from, to) : list.subList(0, 0));
        page.put("total", total);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody OrderInfo order) {
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        orderService.save(order);
        return Result.success();
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@RequestBody OrderInfo order) {
        if (order.getId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        OrderInfo db = orderService.getById(order.getId());
        if (db == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == 1 && (db.getStatus() == null || db.getStatus() != 1)) {
            deductStockWhenPay(db);
            activateCoursePackageWhenPay(db);
            markActivityRegistrationPaid(db, 1);
            order.setPayTime(LocalDateTime.now());
            order.setCompleteTime(LocalDateTime.now());
        } else if (order.getStatus() != null && order.getStatus() == 2) {
            markActivityRegistrationPaid(db, 0);
        }
        orderService.updateById(order);
        return Result.success();
    }

    @PutMapping("/pay/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> pay(@PathVariable Long id) {
        OrderInfo db = orderService.getById(id);
        if (db == null) {
            throw new BusinessException("订单不存在");
        }
        if (db.getStatus() != null && db.getStatus() == 1) {
            return Result.success();
        }
        if (db.getStatus() != null && db.getStatus() == 2) {
            throw new BusinessException("已取消订单不可支付");
        }
        deductStockWhenPay(db);
        activateCoursePackageWhenPay(db);
        markActivityRegistrationPaid(db, 1);
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setStatus(1);
        update.setPayTime(LocalDateTime.now());
        update.setCompleteTime(LocalDateTime.now());
        orderService.updateById(update);
        return Result.success();
    }

    @PutMapping("/refund/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> refund(@PathVariable Long id) {
        OrderInfo db = orderService.getById(id);
        if (db == null) {
            throw new BusinessException("订单不存在");
        }
        if (db.getStatus() != null && db.getStatus() == 2) {
            return Result.success();
        }
        OrderInfo order = new OrderInfo();
        order.setId(id);
        order.setStatus(2);
        order.setCompleteTime(LocalDateTime.now());
        orderService.updateById(order);
        markActivityRegistrationPaid(db, 0);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.success();
    }

    private void deductStockWhenPay(OrderInfo order) {
        if (order.getOrderType() != null
                && ("COURSE".equalsIgnoreCase(order.getOrderType())
                || "ACTIVITY".equalsIgnoreCase(order.getOrderType()))) {
            return;
        }
        if (order.getProductId() == null) {
            return;
        }
        Product product = productService.getById(order.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        int quantity = order.getQuantity() == null || order.getQuantity() <= 0 ? 1 : order.getQuantity();
        int stock = product.getStock() == null ? 0 : product.getStock();
        if (stock < quantity) {
            throw new BusinessException("库存不足");
        }
        product.setStock(stock - quantity);
        productService.updateById(product);
    }

    private void activateCoursePackageWhenPay(OrderInfo order) {
        if (order.getOrderType() == null || !"COURSE".equalsIgnoreCase(order.getOrderType())) {
            return;
        }
        if (order.getProductId() == null) {
            throw new BusinessException("课程信息不能为空");
        }
        Course course = courseService.getById(order.getProductId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        int totalHours = order.getQuantity() == null || order.getQuantity() <= 0 ? 1 : order.getQuantity();
        StudentCoursePackage pkg = new StudentCoursePackage();
        pkg.setStudentId(order.getStudentId());
        pkg.setCourseId(course.getId());
        pkg.setTeacherId(course.getTeacherId());
        pkg.setName(course.getName() + totalHours + "节课时包");
        pkg.setTotalHours(totalHours);
        pkg.setRemainingHours(totalHours);
        pkg.setPrice(order.getActualAmount() != null
                ? order.getActualAmount()
                : (course.getPrice() == null ? BigDecimal.ZERO : course.getPrice().multiply(BigDecimal.valueOf(totalHours))));
        pkg.setStatus(1);
        pkg.setRemark("订单" + order.getOrderNo() + "支付后自动开通");
        packageService.save(pkg);
    }

    private void markActivityRegistrationPaid(OrderInfo order, int paid) {
        if (order == null || order.getOrderType() == null || !"ACTIVITY".equalsIgnoreCase(order.getOrderType())
                || order.getStudentId() == null || order.getProductId() == null) {
            return;
        }
        ActivityRegistration reg = registrationService.getOne(new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getStudentId, order.getStudentId())
                .eq(ActivityRegistration::getActivityId, order.getProductId())
                .notIn(ActivityRegistration::getStatus, 2, 3)
                .orderByDesc(ActivityRegistration::getCreateTime)
                .last("limit 1"), false);
        if (reg == null) {
            return;
        }
        ActivityRegistration update = new ActivityRegistration();
        update.setId(reg.getId());
        update.setFeePaid(paid);
        registrationService.updateById(update);
    }
}
