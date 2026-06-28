package com.piano.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.piano.management.entity.ClassRecord;
import com.piano.management.entity.CourseBooking;
import com.piano.management.entity.StudentCoursePackage;
import com.piano.management.service.ClassRecordService;
import com.piano.management.service.CourseBookingService;
import com.piano.management.service.PackageAvailabilityService;
import com.piano.management.service.StudentCoursePackageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PackageAvailabilityServiceImpl implements PackageAvailabilityService {
    @Resource
    private StudentCoursePackageService packageService;
    @Resource
    private CourseBookingService bookingService;
    @Resource
    private ClassRecordService classRecordService;

    @Override
    public int calculateAvailableHours(Long packageId, Long excludeBookingId) {
        if (packageId == null) {
            return 0;
        }
        StudentCoursePackage pkg = packageService.getById(packageId);
        return calculateAvailableHours(pkg, excludeBookingId);
    }

    @Override
    public int calculateAvailableHours(StudentCoursePackage pkg, Long excludeBookingId) {
        if (pkg == null || pkg.getId() == null) {
            return 0;
        }
        Map<Long, Integer> availableMap = calculateAvailableHoursInternal(
                Collections.singletonList(pkg),
                excludeBookingId == null ? Collections.emptySet() : Collections.singleton(excludeBookingId));
        return availableMap.getOrDefault(pkg.getId(), pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours());
    }

    @Override
    public Map<Long, Integer> calculateAvailableHours(Collection<StudentCoursePackage> packages) {
        return calculateAvailableHoursInternal(packages, Collections.emptySet());
    }

    private Map<Long, Integer> calculateAvailableHoursInternal(Collection<StudentCoursePackage> packages,
                                                               Set<Long> excludeBookingIds) {
        if (packages == null || packages.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, StudentCoursePackage> packageMap = packages.stream()
                .filter(Objects::nonNull)
                .filter(pkg -> pkg.getId() != null)
                .collect(Collectors.toMap(StudentCoursePackage::getId, pkg -> pkg, (a, b) -> a));
        if (packageMap.isEmpty()) {
            return Collections.emptyMap();
        }

        List<CourseBooking> bookings = bookingService.list(new LambdaQueryWrapper<CourseBooking>()
                .in(CourseBooking::getStudentPackageId, packageMap.keySet())
                .in(CourseBooking::getStatus, 0, 1));
        if (!excludeBookingIds.isEmpty()) {
            bookings = bookings.stream()
                    .filter(booking -> booking.getId() == null || !excludeBookingIds.contains(booking.getId()))
                    .collect(Collectors.toList());
        }

        Set<Long> bookingIds = bookings.stream()
                .map(CourseBooking::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> completedBookingIds = Collections.emptySet();
        if (!bookingIds.isEmpty()) {
            completedBookingIds = classRecordService.list(new LambdaQueryWrapper<ClassRecord>()
                            .in(ClassRecord::getBookingId, bookingIds))
                    .stream()
                    .map(ClassRecord::getBookingId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        Map<Long, Integer> reservedHours = new HashMap<>();
        for (CourseBooking booking : bookings) {
            if (booking.getStudentPackageId() == null) {
                continue;
            }
            if (booking.getId() != null && completedBookingIds.contains(booking.getId())) {
                continue;
            }
            int hoursCost = booking.getHoursCost() == null || booking.getHoursCost() <= 0 ? 1 : booking.getHoursCost();
            reservedHours.merge(booking.getStudentPackageId(), hoursCost, Integer::sum);
        }

        Map<Long, Integer> availableMap = new HashMap<>();
        for (StudentCoursePackage pkg : packageMap.values()) {
            int remainingHours = pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours();
            int reserved = reservedHours.getOrDefault(pkg.getId(), 0);
            availableMap.put(pkg.getId(), Math.max(remainingHours - reserved, 0));
        }
        return availableMap;
    }
}
