package com.mifazhan.domain.dto;

import lombok.Data;

/**
 * 对话消息DTO
 */
@Data
public class MessageDTO {
    /**
     * 所属会话ID
     */
    private Long sessionId;

    /**
     * 角色：system / user / assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;
}
