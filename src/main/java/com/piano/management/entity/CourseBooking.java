package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_booking")
public class CourseBooking {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Long studentPackageId;
    private Integer hoursCost;
    private Long classroomId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookingTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime classTime;
    private Integer duration;
    private Integer status;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String classroomName;
    @TableField(exist = false)
    private String packageName;
    @TableField(exist = false)
    private Integer packageRemainingHours;
    @TableField(exist = false)
    private String dateFrom;
    @TableField(exist = false)
    private String dateTo;
    @TableField(exist = false)
    private String sortMode;
}
