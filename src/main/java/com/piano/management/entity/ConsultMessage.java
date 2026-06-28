package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consult_message")
public class ConsultMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Long teacherId;
    private Long studentId;
    private Long senderId;
    /** STUDENT / TEACHER */
    private String senderRole;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
