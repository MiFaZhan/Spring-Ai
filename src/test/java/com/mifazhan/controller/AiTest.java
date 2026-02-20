package com.mifazhan.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AiTest {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void testChatClient() {
        String message = "随便说点什么";
        log.info("开始请求，请求参数message={}", message);
        
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient
                .prompt()
                .user(message)
                .call()
                .content();
        
        log.info("请求参数message={}, 响应结果content={}", message, content);
        
        // 验证返回内容不为空
        assert content != null && !content.isEmpty();
    }
}
