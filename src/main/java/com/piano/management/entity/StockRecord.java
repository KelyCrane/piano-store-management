package com.piano.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stock_record")
public class StockRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String type;
    private Integer quantity;
    private Long operatorId;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String productName;
    @TableField(exist = false)
    private String operatorName;
}
