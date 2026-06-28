package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.ClassRecord;
import com.piano.management.entity.CourseBooking;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.exception.BusinessException;
import com.piano.management.mapper.ClassRecordMapper;
import com.piano.management.service.ClassRecordService;
import com.piano.management.service.CourseBookingService;
import com.piano.management.service.StudentCoursePackageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/classRecord")
public class ClassRecordController {
    @Resource
    private ClassRecordService classRecordService;
    @Resource
    private ClassRecordMapper classRecordMapper;
    @Resource
    private CourseBookingService bookingService;
    @Resource
    private StudentCoursePackageService packageService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) Long teacherId) {
        ClassRecord param = new ClassRecord();
        param.setStudentId(studentId);
        param.setTeacherId(teacherId);
        List<ClassRecord> list = classRecordMapper.selectPageWithNames(param);
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
    public Result<?> save(@RequestBody ClassRecord record) {
        CourseBooking booking = resolveBooking(record);
        if (booking != null) {
            record.setBookingId(booking.getId());
            if (record.getStudentId() == null) {
                record.setStudentId(booking.getStudentId());
            }
            if (record.getTeacherId() == null) {
                record.setTeacherId(booking.getTeacherId());
            }
            if (record.getCourseId() == null) {
                record.setCourseId(booking.getCourseId());
            }
            if (record.getClassroomId() == null) {
                record.setClassroomId(booking.getClassroomId());
            }
            if (record.getClassTime() == null) {
                record.setClassTime(booking.getClassTime());
            }
            if (record.getDuration() == null) {
                record.setDuration(booking.getDuration());
            }
        }

        boolean firstRecordForBooking = booking != null
                && classRecordService.count(new LambdaQueryWrapper<ClassRecord>()
                .eq(ClassRecord::getBookingId, booking.getId())) == 0;

        classRecordService.save(record);
        if (firstRecordForBooking) {
            deductPackageHours(booking);
        }
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody ClassRecord record) {
        classRecordService.updateById(record);
        return Result.success();
    }

    @PutMapping("/evaluate")
    public Result<?> evaluate(@RequestBody ClassRecord record) {
        ClassRecord update = new ClassRecord();
        update.setId(record.getId());
        update.setCourseScore(record.getCourseScore());
        update.setTeacherScore(record.getTeacherScore());
        update.setEvaluation(record.getEvaluation());
        classRecordService.updateById(update);
        return Result.success();
    }

    @DeleteMapping("/evaluation/{id}")
    public Result<?> deleteEvaluation(@PathVariable Long id) {
        classRecordService.update(new UpdateWrapper<ClassRecord>()
                .eq("id", id)
                .set("teacher_score", null)
                .set("evaluation", null));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(@PathVariable Long id) {
        ClassRecord db = classRecordService.getById(id);
        if (db == null) {
            return Result.success();
        }
        boolean shouldRestorePackage = db.getBookingId() != null
                && classRecordService.count(new LambdaQueryWrapper<ClassRecord>()
                .eq(ClassRecord::getBookingId, db.getBookingId())) <= 1;
        if (shouldRestorePackage) {
            CourseBooking booking = bookingService.getById(db.getBookingId());
            restorePackageHours(booking);
        }
        classRecordService.removeById(id);
        return Result.success();
    }

    private CourseBooking resolveBooking(ClassRecord record) {
        if (record.getBookingId() != null) {
            CourseBooking booking = bookingService.getById(record.getBookingId());
            if (booking == null) {
                throw new BusinessException("预约记录不存在");
            }
            return booking;
        }
        if (record.getStudentId() == null || record.getTeacherId() == null
                || record.getCourseId() == null || record.getClassTime() == null) {
            return null;
        }
        return bookingService.getOne(new LambdaQueryWrapper<CourseBooking>()
                .eq(CourseBooking::getStudentId, record.getStudentId())
                .eq(CourseBooking::getTeacherId, record.getTeacherId())
                .eq(CourseBooking::getCourseId, record.getCourseId())
                .eq(CourseBooking::getClassTime, record.getClassTime())
                .eq(CourseBooking::getStatus, 1)
                .orderByDesc(CourseBooking::getCreateTime)
                .last("limit 1"), false);
    }

    private void deductPackageHours(CourseBooking booking) {
        if (booking == null || booking.getStudentPackageId() == null) {
            return;
        }
        StudentCoursePackage pkg = packageService.getById(booking.getStudentPackageId());
        if (pkg == null) {
            throw new BusinessException("课时包不存在");
        }
        int hoursCost = booking.getHoursCost() == null || booking.getHoursCost() <= 0 ? 1 : booking.getHoursCost();
        int remaining = pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours();
        if (remaining < hoursCost) {
            throw new BusinessException("剩余课时不足，无法确认上课记录");
        }
        pkg.setRemainingHours(remaining - hoursCost);
        if (pkg.getRemainingHours() <= 0) {
            pkg.setStatus(0);
        }
        packageService.updateById(pkg);
    }

    private void restorePackageHours(CourseBooking booking) {
        if (booking == null || booking.getStudentPackageId() == null) {
            return;
        }
        StudentCoursePackage pkg = packageService.getById(booking.getStudentPackageId());
        if (pkg == null) {
            return;
        }
        int hoursCost = booking.getHoursCost() == null || booking.getHoursCost() <= 0 ? 1 : booking.getHoursCost();
        int totalHours = pkg.getTotalHours() == null ? Integer.MAX_VALUE : pkg.getTotalHours();
        int remaining = pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours();
        pkg.setRemainingHours(Math.min(remaining + hoursCost, totalHours));
        if (pkg.getRemainingHours() > 0 && (pkg.getStatus() == null || pkg.getStatus() == 0)) {
            pkg.setStatus(1);
        }
        packageService.updateById(pkg);
    }
}
