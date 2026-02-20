package com.mifazhan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mifazhan.domain.convert.SessionConvert;
import com.mifazhan.domain.dto.SessionDTO;
import com.mifazhan.domain.entity.Session;
import com.mifazhan.domain.vo.SessionVO;
import com.mifazhan.service.MessageService;
import com.mifazhan.service.SessionService;
import com.mifazhan.mapper.SessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author MIFAZHAN
* @description 针对表【session(会话表)】的数据库操作Service实现
* @createDate 2026-01-14 14:33:45
*/
@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
    implements SessionService{
    @Autowired
    private SessionConvert sessionConvert;
    @Autowired
    private MessageService messageService;

    @Override
    public List<SessionVO> listSession() {
        List<Session> session = lambdaQuery()
        .orderByDesc(Session::getUpdateTime)
        .list();
        return sessionConvert.toSessionVOList(session);
    }

    @Override
    public SessionVO updateTitle(SessionDTO sessionDTO) {
        Session session = sessionConvert.toSession(sessionDTO);
        this.updateById(session);
        return sessionConvert.toSessionVO(session);
    }

    @Override
    @Transactional
    public Boolean deleteSession(Long sessionId) {
        messageService.removeById(sessionId);
        this.removeById(sessionId);
        return true;
    }
}



