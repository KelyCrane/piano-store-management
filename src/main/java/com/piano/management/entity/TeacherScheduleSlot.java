package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("teacher_schedule_slot")
public class TeacherScheduleSlot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long classroomId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime slotTime;
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
    private String teacherName;
    @TableField(exist = false)
    private String classroomName;
    @TableField(exist = false)
    private Integer bookingCount;
    @TableField(exist = false)
    private Integer slotState;
    @TableField(exist = false)
    private String slotStateText;
    @TableField(exist = false)
    private Boolean bookable;
}
