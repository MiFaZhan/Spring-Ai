package com.mifazhan.domain.dto;

import lombok.Data;

/**
 * WebSocket 返回给前端的 AI 回复片段 DTO
 */
@Data
public class ChatResponseDTO {

    /**
     * AI 回复片段内容
     */
    private String content;

    /**
     * 是否结束（true 表示该轮对话完成）
     */
    private Boolean done;

    /**
     * 错误信息（不为空则表示发生错误）
     */
    private String errorMessage;
}

