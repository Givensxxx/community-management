package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("repair_evaluation")
public class RepairEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer stars;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}