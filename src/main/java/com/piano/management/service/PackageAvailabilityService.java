package com.piano.management.service;

import com.piano.management.entity.StudentCoursePackage;

import java.util.Collection;
import java.util.Map;

public interface PackageAvailabilityService {
    int calculateAvailableHours(Long packageId, Long excludeBookingId);

    int calculateAvailableHours(StudentCoursePackage pkg, Long excludeBookingId);

    Map<Long, Integer> calculateAvailableHours(Collection<StudentCoursePackage> packages);
}
