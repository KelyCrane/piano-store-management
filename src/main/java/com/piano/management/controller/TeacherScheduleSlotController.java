package com.piano.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.piano.management.common.Result;
import com.piano.management.dto.TeacherSlotBatchRequest;
import com.piano.management.entity.Classroom;
import com.piano.management.entity.CourseBooking;
import com.piano.management.entity.SysUser;
import com.piano.management.entity.TeacherScheduleSlot;
import com.piano.management.exception.BusinessException;
import com.piano.management.service.ClassroomService;
import com.piano.management.service.CourseBookingService;
import com.piano.management.service.SysUserService;
import com.piano.management.service.TeacherScheduleSlotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacherSlot")
public class TeacherScheduleSlotController {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Resource
    private TeacherScheduleSlotService slotService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClassroomService classroomService;
    @Resource
    private CourseBookingService bookingService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) Long teacherId,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String dateFrom,
                          @RequestParam(required = false) String dateTo,
                          @RequestParam(required = false) Long studentId) {
        LambdaQueryWrapper<TeacherScheduleSlot> wrapper = new LambdaQueryWrapper<>();
        if (teacherId != null) {
            wrapper.eq(TeacherScheduleSlot::getTeacherId, teacherId);
        }
        if (status != null) {
            wrapper.eq(TeacherScheduleSlot::getStatus, status);
        }
        if (StringUtils.isNotBlank(dateFrom)) {
            wrapper.ge(TeacherScheduleSlot::getSlotTime, parseDate(dateFrom).atStartOfDay());
        }
        if (StringUtils.isNotBlank(dateTo)) {
            wrapper.lt(TeacherScheduleSlot::getSlotTime, parseDate(dateTo).plusDays(1).atStartOfDay());
        }
        applySlotOrder(wrapper);
        Page<TeacherScheduleSlot> page = slotService.page(new Page<>(pageNum, pageSize), wrapper);
        fillSlotDetail(page.getRecords(), studentId);
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<?> all(@RequestParam Long teacherId,
                         @RequestParam(required = false) Long studentId) {
        LambdaQueryWrapper<TeacherScheduleSlot> wrapper = new LambdaQueryWrapper<TeacherScheduleSlot>()
                .eq(TeacherScheduleSlot::getTeacherId, teacherId);
        applySlotOrder(wrapper);
        List<TeacherScheduleSlot> list = slotService.list(wrapper);
        fillSlotDetail(list, studentId);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> save(@RequestBody TeacherScheduleSlot slot) {
        validateSlot(slot, null);
        if (slot.getStatus() == null) {
            slot.setStatus(1);
        }
        slotService.save(slot);
        return Result.success();
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(@RequestBody TeacherScheduleSlot slot) {
        if (slot.getId() == null) {
            throw new BusinessException("时段ID不能为空");
        }
        TeacherScheduleSlot db = slotService.getById(slot.getId());
        if (db == null) {
            throw new BusinessException("时段不存在");
        }
        if (hasActiveBooking(db) && isCoreSlotChanged(db, slot)) {
            throw new BusinessException("该时段已有预约，不能修改时间或时长");
        }
        validateSlot(slot, slot.getId());
        slotService.updateById(slot);
        if (slot.getClassroomId() != null && !Objects.equals(db.getClassroomId(), slot.getClassroomId())) {
            syncSlotClassroomToBookings(db, slot.getClassroomId());
        }
        return Result.success();
    }

    @DeleteMapping("/expired")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteExpired(@RequestParam Long teacherId) {
        List<TeacherScheduleSlot> expiredSlots = slotService.list(new LambdaQueryWrapper<TeacherScheduleSlot>()
                .eq(TeacherScheduleSlot::getTeacherId, teacherId)
                .lt(TeacherScheduleSlot::getSlotTime, LocalDateTime.now()));
        List<Long> removableIds = expiredSlots.stream()
                .filter(slot -> !hasActiveBooking(slot))
                .map(TeacherScheduleSlot::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!removableIds.isEmpty()) {
            slotService.removeByIds(removableIds);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("deleted", removableIds.size());
        data.put("skipped", expiredSlots.size() - removableIds.size());
        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        TeacherScheduleSlot slot = slotService.getById(id);
        if (slot == null) {
            return Result.success();
        }
        if (hasActiveBooking(slot)) {
            throw new BusinessException("该时段已有预约，不能删除");
        }
        slotService.removeById(id);
        return Result.success();
    }

    @PostMapping("/batch")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> batchCreate(@RequestBody TeacherSlotBatchRequest request) {
        if (request == null || request.getTeacherId() == null) {
            throw new BusinessException("老师信息不能为空");
        }
        if (StringUtils.isBlank(request.getStartDate()) || StringUtils.isBlank(request.getEndDate())) {
            throw new BusinessException("请填写起止日期");
        }
        if (request.getWeekdays() == null || request.getWeekdays().isEmpty()) {
            throw new BusinessException("请至少选择一个星期");
        }
        if (request.getTimePoints() == null || request.getTimePoints().isEmpty()) {
            throw new BusinessException("请至少填写一个时间点");
        }
        validateClassroomActive(request.getClassroomId());
        int duration = request.getDuration() == null || request.getDuration() <= 0 ? 60 : request.getDuration();
        int status = request.getStatus() == null ? 1 : request.getStatus();

        LocalDate startDate = parseDate(request.getStartDate());
        LocalDate endDate = parseDate(request.getEndDate());
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        int created = 0;
        int skipped = 0;
        List<TeacherScheduleSlot> slots = new ArrayList<>();
        Set<Integer> weekdays = request.getWeekdays().stream().collect(Collectors.toSet());
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int weekday = DayOfWeek.from(current).getValue();
            if (weekdays.contains(weekday)) {
                for (String timePoint : request.getTimePoints()) {
                    if (StringUtils.isBlank(timePoint)) {
                        continue;
                    }
                    LocalDateTime slotTime = LocalDateTime.of(current, LocalTime.parse(timePoint.trim(), TIME_FORMATTER));
                    if (slotTime.isBefore(LocalDateTime.now())) {
                        skipped++;
                        continue;
                    }
                    boolean exists = slotService.count(new LambdaQueryWrapper<TeacherScheduleSlot>()
                            .eq(TeacherScheduleSlot::getTeacherId, request.getTeacherId())
                            .eq(TeacherScheduleSlot::getSlotTime, slotTime)) > 0;
                    if (exists) {
                        skipped++;
                        continue;
                    }
                    TeacherScheduleSlot slot = new TeacherScheduleSlot();
                    slot.setTeacherId(request.getTeacherId());
                    slot.setClassroomId(request.getClassroomId());
                    slot.setSlotTime(slotTime);
                    slot.setDuration(duration);
                    slot.setStatus(status);
                    slot.setRemark(request.getRemark());
                    slots.add(slot);
                    created++;
                }
            }
            current = current.plusDays(1);
        }
        if (!slots.isEmpty()) {
            slotService.saveBatch(slots);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("created", created);
        data.put("skipped", skipped);
        return Result.success(data);
    }

    private void validateSlot(TeacherScheduleSlot slot, Long excludeId) {
        if (slot == null || slot.getTeacherId() == null) {
            throw new BusinessException("老师信息不能为空");
        }
        if (slot.getSlotTime() == null) {
            throw new BusinessException("请选择可排课时间");
        }
        if (slot.getSlotTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("可排课时间不能早于当前时间");
        }
        if (slot.getDuration() == null || slot.getDuration() <= 0) {
            throw new BusinessException("时长必须大于0");
        }
        validateClassroomActive(slot.getClassroomId());
        LambdaQueryWrapper<TeacherScheduleSlot> wrapper = new LambdaQueryWrapper<TeacherScheduleSlot>()
                .eq(TeacherScheduleSlot::getTeacherId, slot.getTeacherId())
                .eq(TeacherScheduleSlot::getSlotTime, slot.getSlotTime());
        if (excludeId != null) {
            wrapper.ne(TeacherScheduleSlot::getId, excludeId);
        }
        if (slotService.count(wrapper) > 0) {
            throw new BusinessException("该时间段已存在");
        }
    }

    private void applySlotOrder(LambdaQueryWrapper<TeacherScheduleSlot> wrapper) {
        wrapper.last("ORDER BY CASE WHEN slot_time >= NOW() THEN 0 ELSE 1 END ASC, "
                + "CASE WHEN slot_time >= NOW() THEN slot_time END ASC, "
                + "CASE WHEN slot_time < NOW() THEN slot_time END DESC");
    }

    private void fillSlotDetail(List<TeacherScheduleSlot> slots, Long studentId) {
        if (slots == null || slots.isEmpty()) {
            return;
        }
        Map<Long, String> teacherMap = loadTeacherMap(slots);
        Map<Long, Classroom> classroomMap = loadClassroomMap(slots);

        LocalDateTime minSlotTime = slots.stream()
                .map(TeacherScheduleSlot::getSlotTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        LocalDateTime maxSlotTime = slots.stream()
                .map(slot -> slot.getSlotTime().plusMinutes(normalizeDuration(slot.getDuration())))
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().plusDays(7));

        Set<Long> teacherIds = slots.stream()
                .map(TeacherScheduleSlot::getTeacherId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<CourseBooking> teacherBookings = teacherIds.isEmpty() ? Collections.emptyList()
                : bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .in(CourseBooking::getTeacherId, teacherIds)
                .in(CourseBooking::getStatus, 0, 1)
                .ge(CourseBooking::getClassTime, minSlotTime.minusDays(1))
                .lt(CourseBooking::getClassTime, maxSlotTime.plusDays(1)));
        List<CourseBooking> studentBookings = studentId == null ? Collections.emptyList()
                : bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .eq(CourseBooking::getStudentId, studentId)
                .in(CourseBooking::getStatus, 0, 1)
                .ge(CourseBooking::getClassTime, minSlotTime.minusDays(1))
                .lt(CourseBooking::getClassTime, maxSlotTime.plusDays(1)));

        for (TeacherScheduleSlot slot : slots) {
            slot.setTeacherName(teacherMap.get(slot.getTeacherId()));
            Classroom classroom = classroomMap.get(slot.getClassroomId());
            slot.setClassroomName(classroom == null ? null : classroom.getName());

            LocalDateTime slotStart = slot.getSlotTime();
            LocalDateTime slotEnd = slotStart.plusMinutes(normalizeDuration(slot.getDuration()));
            int bookingCount = (int) teacherBookings.stream()
                    .filter(booking -> Objects.equals(booking.getTeacherId(), slot.getTeacherId()))
                    .filter(booking -> isOverlap(slotStart, slotEnd, booking.getClassTime(), bookingEndTime(booking)))
                    .count();
            boolean studentConflict = studentBookings.stream()
                    .anyMatch(booking -> isOverlap(slotStart, slotEnd, booking.getClassTime(), bookingEndTime(booking)));
            slot.setBookingCount(bookingCount);

            if (slot.getStatus() == null || slot.getStatus() != 1) {
                slot.setSlotState(3);
                slot.setSlotStateText("已停用");
                slot.setBookable(false);
            } else if (classroom != null && classroom.getStatus() != null && classroom.getStatus() != 1) {
                slot.setSlotState(3);
                slot.setSlotStateText("教室停用");
                slot.setBookable(false);
            } else if (slot.getSlotTime().isBefore(LocalDateTime.now())) {
                slot.setSlotState(2);
                slot.setSlotStateText("已过期");
                slot.setBookable(false);
            } else if (bookingCount > 0) {
                slot.setSlotState(1);
                slot.setSlotStateText("已约满");
                slot.setBookable(false);
            } else if (studentConflict) {
                slot.setSlotState(4);
                slot.setSlotStateText("本人冲突");
                slot.setBookable(false);
            } else {
                slot.setSlotState(0);
                slot.setSlotStateText("可预约");
                slot.setBookable(true);
            }
        }
    }

    private Map<Long, String> loadTeacherMap(List<TeacherScheduleSlot> slots) {
        Set<Long> teacherIds = slots.stream()
                .map(TeacherScheduleSlot::getTeacherId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (teacherIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return sysUserService.listByIds(teacherIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getName, (a, b) -> a));
    }

    private Map<Long, Classroom> loadClassroomMap(List<TeacherScheduleSlot> slots) {
        Set<Long> classroomIds = slots.stream()
                .map(TeacherScheduleSlot::getClassroomId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (classroomIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return classroomService.listByIds(classroomIds).stream()
                .collect(Collectors.toMap(Classroom::getId, item -> item, (a, b) -> a));
    }

    private void validateClassroomActive(Long classroomId) {
        if (classroomId == null) {
            return;
        }
        Classroom classroom = classroomService.getById(classroomId);
        if (classroom == null) {
            throw new BusinessException("教室不存在");
        }
        if (classroom.getStatus() != null && classroom.getStatus() != 1) {
            throw new BusinessException("教室已停用，不能用于预约");
        }
    }

    private void syncSlotClassroomToBookings(TeacherScheduleSlot slot, Long classroomId) {
        LocalDateTime start = slot.getSlotTime();
        LocalDateTime end = start.plusMinutes(normalizeDuration(slot.getDuration()));
        List<CourseBooking> bookings = bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .eq(CourseBooking::getTeacherId, slot.getTeacherId())
                .in(CourseBooking::getStatus, 0, 1)
                .ge(CourseBooking::getClassTime, start.minusDays(1))
                .lt(CourseBooking::getClassTime, end.plusDays(1)));
        for (CourseBooking booking : bookings) {
            if (!isOverlap(start, end, booking.getClassTime(), bookingEndTime(booking))) {
                continue;
            }
            CourseBooking update = new CourseBooking();
            update.setId(booking.getId());
            update.setClassroomId(classroomId);
            bookingService.updateById(update);
        }
    }

    private boolean hasActiveBooking(TeacherScheduleSlot slot) {
        LocalDateTime start = slot.getSlotTime();
        LocalDateTime end = start.plusMinutes(normalizeDuration(slot.getDuration()));
        return bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                        .eq(CourseBooking::getTeacherId, slot.getTeacherId())
                        .in(CourseBooking::getStatus, 0, 1)
                        .ge(CourseBooking::getClassTime, start.minusDays(1))
                        .lt(CourseBooking::getClassTime, end.plusDays(1)))
                .stream()
                .anyMatch(booking -> isOverlap(start, end, booking.getClassTime(), bookingEndTime(booking)));
    }

    private boolean isCoreSlotChanged(TeacherScheduleSlot db, TeacherScheduleSlot incoming) {
        if (incoming.getSlotTime() != null && !Objects.equals(db.getSlotTime(), incoming.getSlotTime())) {
            return true;
        }
        if (incoming.getDuration() != null && !Objects.equals(db.getDuration(), incoming.getDuration())) {
            return true;
        }
        return incoming.getTeacherId() != null && !Objects.equals(db.getTeacherId(), incoming.getTeacherId());
    }

    private LocalDate parseDate(String value) {
        return LocalDate.parse(value);
    }

    private int normalizeDuration(Integer duration) {
        return duration == null || duration <= 0 ? 60 : duration;
    }

    private LocalDateTime bookingEndTime(CourseBooking booking) {
        if (booking == null || booking.getClassTime() == null) {
            return LocalDateTime.MIN;
        }
        return booking.getClassTime().plusMinutes(normalizeDuration(booking.getDuration()));
    }

    private boolean isOverlap(LocalDateTime start1, LocalDateTime end1,
                              LocalDateTime start2, LocalDateTime end2) {
        return start1 != null && end1 != null && start2 != null && end2 != null
                && start1.isBefore(end2) && start2.isBefore(end1);
    }
}
