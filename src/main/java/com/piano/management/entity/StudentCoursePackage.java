package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("student_course_package")
public class StudentCoursePackage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long teacherId;
    private String name;
    private Integer totalHours;
    private Integer remainingHours;
    private BigDecimal price;
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
    private String courseName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private Integer availableHours;
    @TableField(exist = false)
    private Integer reservedHours;
}
