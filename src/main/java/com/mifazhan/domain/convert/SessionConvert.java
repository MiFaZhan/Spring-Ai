package com.mifazhan.domain.convert;

import com.mifazhan.domain.dto.SessionDTO;
import com.mifazhan.domain.entity.Session;
import com.mifazhan.domain.vo.SessionVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionConvert {
    SessionVO toSessionVO(Session session);

    Session toSession(SessionDTO sessionDTO);

    List<SessionVO> toSessionVOList(List<Session> sessions);
}
