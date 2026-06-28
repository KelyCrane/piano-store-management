package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("class_record")
public class ClassRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookingId;
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Long classroomId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime classTime;
    private Integer duration;
    private String learningStatus;
    private String remark;
    private Integer courseScore;
    private Integer teacherScore;
    private String evaluation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String classroomName;
}
