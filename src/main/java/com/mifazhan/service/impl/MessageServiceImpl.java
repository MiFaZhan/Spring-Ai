package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.dto.MessageDTO;
import com.mifazhan.domain.entity.Message;
import com.mifazhan.mapper.MessageMapper;
import com.mifazhan.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author MIFAZHAN
* @description 针对表【message(对话消息表)】的数据库操作Service实现
* @createDate 2026-01-14 14:33:45
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Override
    public List<MessageDTO> getBySessionId(Long sessionId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSessionId, sessionId)
                .orderByAsc(Message::getCreationTime);
        List<Message> messages = this.list(wrapper);
        return messages.stream()
                .map(message -> {
                    MessageDTO dto = new MessageDTO();
                    dto.setSessionId(message.getSessionId());
                    dto.setRole(message.getRole());
                    dto.setContent(message.getContent());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

