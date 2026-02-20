package com.mifazhan.domain.dto;

import lombok.Data;

/**
 * 聊天消息 DTO
 */
@Data
public class ChatMessageDTO {

    /**
     * 会话ID（新会话时可为空）
     */
    private Long sessionId;

    /**
     * 用户输入内容
     */
    private String content;
}

