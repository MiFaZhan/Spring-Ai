package com.mifazhan.controller;

import com.mifazhan.domain.dto.MessageDTO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.domain.vo.SessionVO;
import com.mifazhan.service.MessageService;
import com.mifazhan.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取指定会话的所有消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/{sessionId}")
    public Result<List<MessageDTO>> getHistory(@PathVariable Long sessionId) {
        return Result.success(messageService.getBySessionId(sessionId));
    }
}
