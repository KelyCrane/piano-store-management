package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.common.Result;
import com.piano.management.entity.Classroom;
import com.piano.management.dto.CourseBookingBatchRequest;
import com.piano.management.entity.ClassRecord;
import com.piano.management.entity.Course;
import com.piano.management.entity.CourseBooking;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.entity.SysUser;
import com.piano.management.entity.TeacherScheduleSlot;
import com.piano.management.exception.BusinessException;
import com.piano.management.mapper.CourseBookingMapper;
import com.piano.management.service.ClassRecordService;
import com.piano.management.service.ClassroomService;
import com.piano.management.service.CourseBookingService;
import com.piano.management.service.CourseService;
import com.piano.management.service.PackageAvailabilityService;
import com.piano.management.service.StudentCoursePackageService;
import com.piano.management.service.SysUserService;
import com.piano.management.service.TeacherScheduleSlotService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
public class CourseBookingController {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private CourseBookingService bookingService;
    @Resource
    private CourseBookingMapper bookingMapper;
    @Resource
    private StudentCoursePackageService packageService;
    @Resource
    private PackageAvailabilityService packageAvailabilityService;
    @Resource
    private TeacherScheduleSlotService slotService;
    @Resource
    private CourseService courseService;
    @Resource
    private ClassroomService classroomService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClassRecordService classRecordService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long studentId,
                          @RequestParam(required = false) Long teacherId,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String dateFrom,
                          @RequestParam(required = false) String dateTo,
                          @RequestParam(required = false) String sortMode) {
        expirePendingBookings();
        CourseBooking param = new CourseBooking();
        param.setStudentId(studentId);
        param.setTeacherId(teacherId);
        param.setStatus(status);
        param.setDateFrom(dateFrom);
        param.setDateTo(dateTo);
        param.setSortMode(sortMode);
        List<CourseBooking> list = bookingMapper.selectPageWithNames(param);
        int total = list.size();
        int from = (pageNum - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<CourseBooking> records = from < total ? list.subList(from, to) : list.subList(0, 0);
        Map<String, Object> page = new HashMap<>();
        page.put("records", records);
        page.put("total", total);
        return Result.success(page);
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody CourseBooking booking) {
        prepareBookingForCreate(booking);
        bookingService.save(booking);
        return Result.success();
    }

    @PostMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchSave(@RequestBody CourseBookingBatchRequest request) {
        if (request == null || request.getClassTimes() == null || request.getClassTimes().isEmpty()) {
            throw new BusinessException("请至少选择一个上课时间");
        }

        CourseBooking baseBooking = new CourseBooking();
        baseBooking.setStudentId(request.getStudentId());
        baseBooking.setTeacherId(request.getTeacherId());
        baseBooking.setCourseId(request.getCourseId());
        baseBooking.setStudentPackageId(request.getStudentPackageId());
        baseBooking.setHoursCost(request.getHoursCost());
        baseBooking.setRemark(request.getRemark());
        fillBookingDefaults(baseBooking);

        int hoursCost = normalizeHoursCost(baseBooking.getHoursCost());
        baseBooking.setHoursCost(hoursCost);

        List<LocalDateTime> classTimes = parseDistinctClassTimes(request.getClassTimes());
        StudentCoursePackage pkg = validateAndFillPackage(baseBooking, true, hoursCost * classTimes.size(), null);
        validateTeacherStudentLimit(baseBooking.getTeacherId(), baseBooking.getStudentId(), null);

        List<CourseBooking> bookings = new ArrayList<>();
        for (LocalDateTime classTime : classTimes) {
            CourseBooking booking = new CourseBooking();
            booking.setStudentId(baseBooking.getStudentId());
            booking.setTeacherId(baseBooking.getTeacherId());
            booking.setCourseId(baseBooking.getCourseId());
            booking.setStudentPackageId(pkg.getId());
            booking.setHoursCost(hoursCost);
            booking.setDuration(baseBooking.getDuration());
            booking.setRemark(baseBooking.getRemark());
            booking.setStatus(0);
            booking.setClassTime(classTime);
            validateClassTime(classTime);
            TeacherScheduleSlot slot = validateScheduleSlot(booking.getTeacherId(), classTime, true);
            applySlotInfo(booking, slot, true);
            validateConflict(booking.getTeacherId(), booking.getStudentId(), booking.getClassTime(), booking.getDuration(), null);
            booking.setBookingTime(LocalDateTime.now());
            bookings.add(booking);
        }
        bookingService.saveBatch(bookings);

        Map<String, Object> data = new HashMap<>();
        data.put("count", bookings.size());
        return Result.success(data);
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@RequestBody CourseBooking booking) {
        if (booking.getId() == null) {
            throw new BusinessException("预约ID不能为空");
        }
        CourseBooking db = bookingService.getById(booking.getId());
        if (db == null) {
            throw new BusinessException("预约记录不存在");
        }
        fillBookingDefaults(booking);
        if (isClassTimeChanged(db, booking)) {
            validateClassTime(booking.getClassTime());
        }

        CourseBooking merged = mergeBooking(db, booking);
        Integer targetStatus = merged.getStatus();
        if (booking.getStatus() != null && isActiveBookingStatus(targetStatus)) {
            validateClassTime(merged.getClassTime());
        }
        if (isActiveBookingStatus(targetStatus)) {
            validateTeacherStudentLimit(merged.getTeacherId(), merged.getStudentId(), booking.getId());
        }
        if (shouldValidatePackage(db, booking, targetStatus)) {
            StudentCoursePackage pkg = validateAndFillPackage(merged, true, normalizeHoursCost(merged.getHoursCost()), booking.getId());
            if (booking.getStudentPackageId() == null) {
                booking.setStudentPackageId(pkg.getId());
            }
            if (booking.getHoursCost() == null) {
                booking.setHoursCost(merged.getHoursCost());
            }
            if (booking.getTeacherId() == null && merged.getTeacherId() != null) {
                booking.setTeacherId(merged.getTeacherId());
            }
            if (booking.getDuration() == null && merged.getDuration() != null) {
                booking.setDuration(merged.getDuration());
            }
        }

        boolean slotChanged = booking.getClassTime() != null || booking.getTeacherId() != null;
        boolean shouldRefreshSlotInfo = slotChanged || (targetStatus != null && targetStatus == 1 && merged.getClassroomId() == null);
        if (shouldRefreshSlotInfo) {
            TeacherScheduleSlot slot = validateScheduleSlot(merged.getTeacherId(), merged.getClassTime(), true);
            applySlotInfo(booking, slot, true);
            applySlotInfo(merged, slot, true);
        }
        if (shouldValidateConflict(booking, targetStatus)) {
            validateConflict(merged.getTeacherId(), merged.getStudentId(), merged.getClassTime(), merged.getDuration(), booking.getId());
        }
        bookingService.updateById(booking);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> cancel(@PathVariable Long id) {
        CourseBooking db = bookingService.getById(id);
        if (db == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (db.getStatus() != null && db.getStatus() == 3) {
            return Result.success();
        }
        if (!isActiveBookingStatus(db.getStatus())) {
            throw new BusinessException("当前预约状态不能取消");
        }
        if (db.getClassTime() != null && db.getClassTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("已过上课时间，不能取消预约");
        }
        if (hasClassRecord(id)) {
            throw new BusinessException("预约已有上课记录，不能取消");
        }
        CourseBooking update = new CourseBooking();
        update.setId(id);
        update.setStatus(3);
        bookingService.updateById(update);
        return Result.success();
    }

    @PutMapping("/reschedule/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> reschedule(@PathVariable Long id, @RequestBody CourseBooking booking) {
        CourseBooking db = bookingService.getById(id);
        if (db == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!isActiveBookingStatus(db.getStatus())) {
            throw new BusinessException("当前预约状态不能改约");
        }
        if (db.getClassTime() != null && db.getClassTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("已过上课时间，不能改约");
        }
        if (hasClassRecord(id)) {
            throw new BusinessException("预约已有上课记录，不能改约");
        }
        booking.setId(id);
        booking.setStatus(0);
        return update(booking);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bookingService.removeById(id);
        return Result.success();
    }

    private void prepareBookingForCreate(CourseBooking booking) {
        if (booking.getClassTime() == null && booking.getBookingTime() != null) {
            booking.setClassTime(booking.getBookingTime());
        }
        fillBookingDefaults(booking);
        validateClassTime(booking.getClassTime());
        StudentCoursePackage pkg = validateAndFillPackage(booking, true, normalizeHoursCost(booking.getHoursCost()), null);
        if (booking.getTeacherId() == null) {
            booking.setTeacherId(pkg.getTeacherId());
        }
        validateTeacherStudentLimit(booking.getTeacherId(), booking.getStudentId(), null);
        TeacherScheduleSlot slot = validateScheduleSlot(booking.getTeacherId(), booking.getClassTime(), true);
        applySlotInfo(booking, slot, true);
        validateConflict(booking.getTeacherId(), booking.getStudentId(), booking.getClassTime(), booking.getDuration(), null);
        booking.setBookingTime(LocalDateTime.now());
        if (booking.getStatus() == null) {
            booking.setStatus(0);
        }
    }

    private void fillBookingDefaults(CourseBooking booking) {
        if (booking == null || booking.getCourseId() == null) {
            return;
        }
        Course course = courseService.getById(booking.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        if (booking.getTeacherId() == null) {
            booking.setTeacherId(course.getTeacherId());
        }
        if (booking.getDuration() == null) {
            booking.setDuration(course.getDuration() == null ? 60 : course.getDuration());
        }
    }

    private void validateClassTime(LocalDateTime classTime) {
        if (classTime == null) {
            throw new BusinessException("请选择上课时间");
        }
        if (classTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("上课时间不能早于当前时间");
        }
    }

    private TeacherScheduleSlot validateScheduleSlot(Long teacherId, LocalDateTime classTime, boolean required) {
        if (teacherId == null || classTime == null) {
            if (required) {
                throw new BusinessException("老师可排课时间不存在");
            }
            return null;
        }
        TeacherScheduleSlot slot = slotService.getOne(new LambdaQueryWrapper<TeacherScheduleSlot>()
                .eq(TeacherScheduleSlot::getTeacherId, teacherId)
                .eq(TeacherScheduleSlot::getSlotTime, classTime)
                .eq(TeacherScheduleSlot::getStatus, 1)
                .last("limit 1"), false);
        if (required && slot == null) {
            throw new BusinessException("所选时间不在老师可排课时间内");
        }
        if (slot != null && slot.getClassroomId() != null) {
            validateClassroomActive(slot.getClassroomId());
        }
        return slot;
    }

    private void validateClassroomActive(Long classroomId) {
        Classroom classroom = classroomService.getById(classroomId);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }
        if (classroom.getStatus() != null && classroom.getStatus() != 1) {
            throw new BusinessException("教室已停用，不能用于预约");
        }
    }

    private void applySlotInfo(CourseBooking booking, TeacherScheduleSlot slot, boolean overwrite) {
        if (booking == null || slot == null) {
            return;
        }
        if ((overwrite || booking.getDuration() == null) && slot.getDuration() != null) {
            booking.setDuration(slot.getDuration());
        }
        if ((overwrite || booking.getClassroomId() == null) && slot.getClassroomId() != null) {
            booking.setClassroomId(slot.getClassroomId());
        }
    }

    private void validateConflict(Long teacherId, Long studentId, LocalDateTime classTime, Integer duration, Long excludeId) {
        if (classTime == null) {
            return;
        }
        int minutes = duration == null || duration <= 0 ? 60 : duration;
        LocalDateTime endTime = classTime.plusMinutes(minutes);
        if (teacherId != null) {
            List<CourseBooking> teacherBookings = loadPotentialConflicts(teacherId, null, classTime, endTime, excludeId);
            boolean teacherConflict = teacherBookings.stream()
                    .anyMatch(booking -> isOverlap(classTime, endTime, booking.getClassTime(), bookingEndTime(booking)));
            if (teacherConflict) {
                throw new BusinessException("老师该时段已被预约");
            }
        }
        if (studentId != null) {
            List<CourseBooking> studentBookings = loadPotentialConflicts(null, studentId, classTime, endTime, excludeId);
            boolean studentConflict = studentBookings.stream()
                    .anyMatch(booking -> isOverlap(classTime, endTime, booking.getClassTime(), bookingEndTime(booking)));
            if (studentConflict) {
                throw new BusinessException("该时段您已有其他课程");
            }
        }
    }

    private List<CourseBooking> loadPotentialConflicts(Long teacherId, Long studentId,
                                                       LocalDateTime startTime, LocalDateTime endTime,
                                                       Long excludeId) {
        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<CourseBooking>()
                .in(CourseBooking::getStatus, 0, 1)
                .ge(CourseBooking::getClassTime, startTime.minusDays(1))
                .lt(CourseBooking::getClassTime, endTime.plusDays(1));
        if (teacherId != null) {
            wrapper.eq(CourseBooking::getTeacherId, teacherId);
        }
        if (studentId != null) {
            wrapper.eq(CourseBooking::getStudentId, studentId);
        }
        if (excludeId != null) {
            wrapper.ne(CourseBooking::getId, excludeId);
        }
        return bookingService.list(wrapper);
    }

    private CourseBooking mergeBooking(CourseBooking db, CourseBooking incoming) {
        CourseBooking merged = new CourseBooking();
        merged.setId(db.getId());
        merged.setStudentId(incoming.getStudentId() != null ? incoming.getStudentId() : db.getStudentId());
        merged.setTeacherId(incoming.getTeacherId() != null ? incoming.getTeacherId() : db.getTeacherId());
        merged.setCourseId(incoming.getCourseId() != null ? incoming.getCourseId() : db.getCourseId());
        merged.setStudentPackageId(incoming.getStudentPackageId() != null ? incoming.getStudentPackageId() : db.getStudentPackageId());
        merged.setHoursCost(incoming.getHoursCost() != null ? incoming.getHoursCost() : db.getHoursCost());
        merged.setStatus(incoming.getStatus() != null ? incoming.getStatus() : db.getStatus());
        merged.setClassTime(incoming.getClassTime() != null ? incoming.getClassTime() : db.getClassTime());
        merged.setDuration(incoming.getDuration() != null ? incoming.getDuration() : db.getDuration());
        merged.setClassroomId(incoming.getClassroomId() != null ? incoming.getClassroomId() : db.getClassroomId());
        return merged;
    }

    private boolean shouldValidatePackage(CourseBooking db, CourseBooking incoming, Integer targetStatus) {
        if (!isActiveBookingStatus(targetStatus)) {
            return false;
        }
        if (incoming.getStudentPackageId() != null || incoming.getHoursCost() != null
                || incoming.getStudentId() != null || incoming.getCourseId() != null
                || incoming.getTeacherId() != null || incoming.getStatus() != null) {
            return true;
        }
        return db.getStudentPackageId() != null;
    }

    private boolean shouldValidateConflict(CourseBooking booking, Integer targetStatus) {
        if (!isActiveBookingStatus(targetStatus)) {
            return false;
        }
        return booking.getClassTime() != null
                || booking.getStatus() != null
                || booking.getTeacherId() != null
                || booking.getStudentId() != null
                || booking.getDuration() != null;
    }

    private StudentCoursePackage validateAndFillPackage(CourseBooking booking,
                                                        boolean autoSelectWhenMissing,
                                                        int totalRequiredHours,
                                                        Long excludeBookingId) {
        booking.setHoursCost(normalizeHoursCost(booking.getHoursCost()));

        if (booking.getStudentId() == null) {
            throw new BusinessException("学生信息不能为空");
        }
        if (booking.getCourseId() == null) {
            throw new BusinessException("课程信息不能为空");
        }

        StudentCoursePackage pkg = null;
        if (booking.getStudentPackageId() != null) {
            pkg = packageService.getById(booking.getStudentPackageId());
        } else if (autoSelectWhenMissing) {
            List<StudentCoursePackage> packages = packageService.list(new LambdaQueryWrapper<StudentCoursePackage>()
                    .eq(StudentCoursePackage::getStudentId, booking.getStudentId())
                    .eq(StudentCoursePackage::getCourseId, booking.getCourseId())
                    .eq(StudentCoursePackage::getStatus, 1)
                    .orderByDesc(StudentCoursePackage::getRemainingHours)
                    .orderByDesc(StudentCoursePackage::getCreateTime));
            for (StudentCoursePackage candidate : packages) {
                if (booking.getTeacherId() != null && candidate.getTeacherId() != null
                        && !Objects.equals(booking.getTeacherId(), candidate.getTeacherId())) {
                    continue;
                }
                int availableHours = packageAvailabilityService.calculateAvailableHours(candidate, excludeBookingId);
                if (availableHours >= totalRequiredHours) {
                    pkg = candidate;
                    break;
                }
            }
        }
        if (pkg == null) {
            throw new BusinessException("请先购买该课程课时包");
        }

        if (!Objects.equals(pkg.getStudentId(), booking.getStudentId())) {
            throw new BusinessException("课时包与学生不匹配");
        }
        if (!Objects.equals(pkg.getCourseId(), booking.getCourseId())) {
            throw new BusinessException("课时包与课程不匹配");
        }
        if (pkg.getStatus() != null && pkg.getStatus() != 1) {
            throw new BusinessException("课时包已停用");
        }
        if (booking.getTeacherId() != null && pkg.getTeacherId() != null
                && !Objects.equals(booking.getTeacherId(), pkg.getTeacherId())) {
            throw new BusinessException("课时包与老师不匹配");
        }
        int availableHours = packageAvailabilityService.calculateAvailableHours(pkg, excludeBookingId);
        if (availableHours < totalRequiredHours) {
            throw new BusinessException("可排课时不足，当前仅剩" + availableHours + "节可排");
        }

        booking.setStudentPackageId(pkg.getId());
        if (booking.getTeacherId() == null && pkg.getTeacherId() != null) {
            booking.setTeacherId(pkg.getTeacherId());
        }
        return pkg;
    }

    private void validateTeacherStudentLimit(Long teacherId, Long studentId, Long excludeBookingId) {
        if (teacherId == null || studentId == null) {
            return;
        }
        SysUser teacher = sysUserService.getById(teacherId);
        if (teacher == null || teacher.getTeacherStudentLimit() == null || teacher.getTeacherStudentLimit() <= 0) {
            return;
        }
        List<CourseBooking> activeBookings = bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .eq(CourseBooking::getTeacherId, teacherId)
                .in(CourseBooking::getStatus, 0, 1));
        Set<Long> studentIds = new HashSet<>();
        for (CourseBooking activeBooking : activeBookings) {
            if (activeBooking.getId() != null && Objects.equals(activeBooking.getId(), excludeBookingId)) {
                continue;
            }
            if (activeBooking.getStudentId() != null) {
                studentIds.add(activeBooking.getStudentId());
            }
        }
        if (!studentIds.contains(studentId) && studentIds.size() >= teacher.getTeacherStudentLimit()) {
            throw new BusinessException("该老师当前学生数量已达上限");
        }
    }

    private boolean hasClassRecord(Long bookingId) {
        return classRecordService.count(new LambdaQueryWrapper<ClassRecord>()
                .eq(ClassRecord::getBookingId, bookingId)) > 0;
    }

    private boolean isActiveBookingStatus(Integer status) {
        return status != null && (status == 0 || status == 1);
    }

    private void expirePendingBookings() {
        List<CourseBooking> expired = bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .eq(CourseBooking::getStatus, 0)
                .lt(CourseBooking::getClassTime, LocalDateTime.now()));
        for (CourseBooking booking : expired) {
            CourseBooking update = new CourseBooking();
            update.setId(booking.getId());
            update.setStatus(4);
            bookingService.updateById(update);
        }
    }

    private boolean isClassTimeChanged(CourseBooking db, CourseBooking incoming) {
        return incoming.getClassTime() != null && !Objects.equals(db.getClassTime(), incoming.getClassTime());
    }

    private List<LocalDateTime> parseDistinctClassTimes(List<String> classTimes) {
        Set<LocalDateTime> set = new LinkedHashSet<>();
        for (String classTime : classTimes) {
            if (classTime == null || classTime.trim().isEmpty()) {
                continue;
            }
            set.add(LocalDateTime.parse(classTime.trim(), DATE_TIME_FORMATTER));
        }
        if (set.isEmpty()) {
            throw new BusinessException("请至少选择一个上课时间");
        }
        return new ArrayList<>(set);
    }

    private int normalizeHoursCost(Integer hoursCost) {
        return hoursCost == null || hoursCost <= 0 ? 1 : hoursCost;
    }

    private LocalDateTime bookingEndTime(CourseBooking booking) {
        if (booking == null || booking.getClassTime() == null) {
            return LocalDateTime.MIN;
        }
        int minutes = booking.getDuration() == null || booking.getDuration() <= 0 ? 60 : booking.getDuration();
        return booking.getClassTime().plusMinutes(minutes);
    }

    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1,
                              LocalDateTime start2, LocalDateTime end2) {
        return start1 != null && end1 != null && start2 != null && end2 != null
                && start1.isBefore(end2) && start2.isBefore(end1);
    }
}
