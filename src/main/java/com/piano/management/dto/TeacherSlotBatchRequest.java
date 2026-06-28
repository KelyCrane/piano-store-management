package com.piano.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeacherSlotBatchRequest {
    private Long teacherId;
    private Long classroomId;
    private String startDate;
    private String endDate;
    private List<Integer> weekdays;
    private List<String> timePoints;
    private Integer duration;
    private Integer status;
    private String remark;
}
