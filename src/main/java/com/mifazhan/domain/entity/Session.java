package com.mifazhan.domain.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 会话表
 * @TableName session
 */
@Data
@TableName("session")
public class Session {
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long sessionId;

    /**
     * 会话标题（首轮对话后生成）
     */
    private String title;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime creationTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0否 1是
     */
    private Integer deleted;
}