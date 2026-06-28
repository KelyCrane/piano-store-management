package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("teacher_qualification")
public class TeacherQualification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private String type;
    private String name;
    private String imageUrl;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 教师姓名（联表查询填充） */
    @TableField(exist = false)
    private String teacherName;
}
