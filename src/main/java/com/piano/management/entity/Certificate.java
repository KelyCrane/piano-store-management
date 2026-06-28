package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("certificate")
public class Certificate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long activityId;
    private String type;
    private String name;
    private String imageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate obtainTime;
    private Long teacherId;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String activityTitle;
}
