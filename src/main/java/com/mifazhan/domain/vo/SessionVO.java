package com.mifazhan.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionVO {
    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 会话标题（首轮对话后生成）
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime creationTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
