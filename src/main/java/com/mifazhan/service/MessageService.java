package com.mifazhan.service;

import com.mifazhan.domain.dto.MessageDTO;
import com.mifazhan.domain.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author MIFAZHAN
* @description 针对表【message(对话消息表)】的数据库操作Service
* @createDate 2026-01-14 14:33:45
*/
public interface MessageService extends IService<Message> {

    /**
     * 根据会话ID获取消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<MessageDTO> getBySessionId(Long sessionId);
}
