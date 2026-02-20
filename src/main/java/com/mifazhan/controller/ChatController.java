package com.mifazhan.controller;

import com.mifazhan.domain.dto.ChatMessageDTO;
import com.mifazhan.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    // 使用单独的线程池来处理 SSE 请求，避免阻塞 Servlet 线程
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final String MODEL_NAME = "glm-4.5-flash";

    /**
     * 发送消息并获取流式响应
     *
     * @param chatMessageDTO 聊天消息
     * @return SseEmitter
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody ChatMessageDTO chatMessageDTO) {
        // 设置超时时间，0 表示不过期
        SseEmitter emitter = new SseEmitter(0L);

        executorService.execute(() -> {
            try {
                chatService.chatStream(
                        chatMessageDTO.getSessionId(),
                        chatMessageDTO.getContent(),
                        chunk -> sendChunk(emitter, chunk, false),
                        () -> {
                            sendChunk(emitter, "", true);
                            emitter.complete();
                        },
                        error -> {
                            sendError(emitter, error);
                            emitter.complete();
                        }
                );
            } catch (Exception e) {
                log.error("SSE 处理异常", e);
                sendError(emitter, "系统异常: " + e.getMessage());
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private void sendChunk(SseEmitter emitter, String content, boolean done) {
        try {
            if (done) {
                emitter.send(SseEmitter.event().data("[DONE]"));
            } else {
                long created = System.currentTimeMillis() / 1000;
                String json = String.format(
                        "{\"id\":\"chatcmpl-%d\",\"object\":\"chat.completion.chunk\",\"created\":%d,\"model\":\"%s\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"%s\"},\"finish_reason\":null}]}",
                        System.currentTimeMillis(),
                        created,
                        MODEL_NAME,
                        escapeJson(content)
                );
                emitter.send(SseEmitter.event().data(json));
            }
        } catch (IOException e) {
            log.warn("SSE 发送消息失败: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    private void sendError(SseEmitter emitter, String errorMessage) {
        try {
            String json = String.format("{\"error\":{\"message\":\"%s\",\"type\":\"invalid_request_error\"}}", escapeJson(errorMessage));
            emitter.send(SseEmitter.event().data(json));
            emitter.complete();
        } catch (IOException e) {
            log.warn("SSE 发送错误消息失败: {}", e.getMessage());
            emitter.completeWithError(e);
        }
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
