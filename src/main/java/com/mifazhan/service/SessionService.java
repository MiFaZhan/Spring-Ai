package com.mifazhan.service;

import com.mifazhan.domain.dto.SessionDTO;
import com.mifazhan.domain.entity.Session;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mifazhan.domain.vo.SessionVO;

import java.util.List;

/**
* @author MIFAZHAN
* @description 针对表【session(会话表)】的数据库操作Service
* @createDate 2026-01-14 14:33:45
*/
public interface SessionService extends IService<Session> {

    List<SessionVO> listSession();

    /**
     * 更新会话标题
     */
    SessionVO updateTitle(SessionDTO sessionDTO);

    Boolean deleteSession(Long sessionId);
}
