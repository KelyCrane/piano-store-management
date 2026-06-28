package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long studentId;
    private Integer status;
    private Integer feePaid;
    private Integer passed;
    @TableField("`rank`")
    private Integer rank;
    private String awardLevel;
    private Long teacherId;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String activityTitle;
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String activityType;
}
