package com.piano.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseBookingBatchRequest {
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Long studentPackageId;
    private Integer hoursCost;
    private String remark;
    private List<String> classTimes;
}
