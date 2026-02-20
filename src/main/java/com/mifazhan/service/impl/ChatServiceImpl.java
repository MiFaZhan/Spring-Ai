package com.mifazhan.service.impl;

import com.mifazhan.domain.entity.Message;
import com.mifazhan.domain.entity.Session;
import com.mifazhan.service.ChatService;
import com.mifazhan.service.MessageService;
import com.mifazhan.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AI 聊天服务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final SessionService sessionService;
    private final MessageService messageService;
    private final ChatClient chatClient;

    @Autowired
    public ChatServiceImpl(SessionService sessionService,
                           MessageService messageService,
                           ChatClient.Builder chatClientBuilder) {
        this.sessionService = sessionService;
        this.messageService = messageService;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 处理流式对话
     *
     * @param sessionId     会话ID，新会话时可以为 null
     * @param userMessage   用户输入消息内容
     * @param chunkCallback 流式片段回调
     * @param doneCallback  完成回调
     */
    @Override
//    @Transactional
    public void chatStream(Long sessionId, String userMessage, ChunkCallback chunkCallback, DoneCallback doneCallback, ErrorCallback errorCallback) {
        if (userMessage == null || userMessage.isBlank()) {
            log.warn("用户消息为空，忽略本次请求");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. 处理会话：新建或检查已有会话
        Session session;
        if (sessionId == null) {
            session = new Session();
            session.setCreationTime(now);
            session.setUpdateTime(now);
            session.setDeleted(0);
            session.setTitle(generateTitleFromMessage(userMessage));
            sessionService.save(session);
            log.info("创建新会话，sessionId={}", session.getSessionId());
        } else {
            session = sessionService.getById(sessionId);
            if (session == null || (session.getDeleted() != null && session.getDeleted() == 1)) {
                throw new IllegalArgumentException("会话不存在或已删除，sessionId=" + sessionId);
            }
            session.setUpdateTime(now);
            sessionService.updateById(session);
        }
        Long currentSessionId = session.getSessionId();

        // 2. 保存用户消息
        Message userMsg = new Message();
        userMsg.setSessionId(currentSessionId);
        userMsg.setRole("user");
        userMsg.setContent(userMessage);
        userMsg.setDeleted(0);
        messageService.save(userMsg);

        // 3. 调用大模型进行流式输出
        StringBuilder fullAiContent = new StringBuilder();

        chatClient.prompt()
                .user(userMessage)
                .stream()
                .content()
                .subscribe(
                        chunk -> {
                            try {
                                fullAiContent.append(chunk);
                                if (chunkCallback != null) {
                                    chunkCallback.onChunk(chunk);
                                }
                            } catch (Exception e) {
                                log.error("处理 AI 片段异常", e);
                                if (errorCallback != null) {
                                    errorCallback.onError("处理消息片段异常: " + e.getMessage());
                                }
                            }
                        },
                        error -> {
                            log.error("调用 AI 服务异常", error);
                            if (errorCallback != null) {
                                errorCallback.onError("AI 服务异常: " + error.getMessage());
                            }
                        },
                        () -> {
                            try {
                                // 4. 流结束，保存 AI 完整回复
                                LocalDateTime finishTime = LocalDateTime.now();
                                Message assistantMsg = new Message();
                                assistantMsg.setSessionId(currentSessionId);
                                assistantMsg.setRole("assistant");
                                assistantMsg.setContent(fullAiContent.toString());
                                assistantMsg.setDeleted(0);
                                messageService.save(assistantMsg);

                                Session updateSession = sessionService.getById(currentSessionId);
                                if (updateSession != null) {
                                    updateSession.setUpdateTime(finishTime);
                                    sessionService.updateById(updateSession);
                                }

                                if (doneCallback != null) {
                                    doneCallback.onDone();
                                }
                                log.info("AI 回复已保存，sessionId={}", currentSessionId);
                            } catch (Exception e) {
                                log.error("保存 AI 消息或触发完成回调异常", e);
                                if (errorCallback != null) {
                                    errorCallback.onError("保存消息或完成回调异常: " + e.getMessage());
                                }
                            }
                        }
                );
    }

    private String generateTitleFromMessage(String message) {
        int maxLength = 30;
        String trimmed = message.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength) + "...";
    }
}

