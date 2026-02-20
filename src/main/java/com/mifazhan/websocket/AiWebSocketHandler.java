package com.mifazhan.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifazhan.domain.dto.ChatMessageDTO;
import com.mifazhan.domain.dto.ChatResponseDTO;
import com.mifazhan.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * AI 聊天 WebSocket 处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) {
        log.info("WebSocket 连接建立，sessionId={}", wsSession.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) {
        try {
            String payload = message.getPayload();
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

            chatService.chatStream(
                    chatMessageDTO.getSessionId(),
                    chatMessageDTO.getContent(),
                    chunk -> sendChunk(wsSession, chunk, false),
                    () -> sendChunk(wsSession, "", true),
                    error -> sendError(wsSession, error)
            );
        } catch (Exception e) {
            log.error("处理 WebSocket 消息异常", e);
            sendError(wsSession, "系统异常: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) {
        log.info("WebSocket 连接关闭，sessionId={}，status={}", wsSession.getId(), status);
    }

    private void sendChunk(WebSocketSession wsSession, String content, boolean done) {
        if (!wsSession.isOpen()) {
            return;
        }
        try {
            ChatResponseDTO responseDTO = new ChatResponseDTO();
            responseDTO.setContent(content);
            responseDTO.setDone(done);
            String json = objectMapper.writeValueAsString(responseDTO);
            synchronized (wsSession) {
                wsSession.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            log.error("通过 WebSocket 发送消息异常", e);
        }
    }

    private void sendError(WebSocketSession wsSession, String errorMessage) {
        if (!wsSession.isOpen()) {
            return;
        }
        try {
            ChatResponseDTO responseDTO = new ChatResponseDTO();
            responseDTO.setDone(true);
            responseDTO.setErrorMessage(errorMessage);
            String json = objectMapper.writeValueAsString(responseDTO);
            synchronized (wsSession) {
                wsSession.sendMessage(new TextMessage(json));
            }
        } catch (Exception e) {
            log.error("通过 WebSocket 发送错误消息异常", e);
        }
    }
}

