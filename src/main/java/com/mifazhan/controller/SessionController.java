package com.mifazhan.controller;

import com.mifazhan.domain.dto.SessionDTO;
import com.mifazhan.domain.vo.Result;
import com.mifazhan.domain.vo.SessionVO;
import com.mifazhan.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/session")
public class SessionController {
    @Autowired
    private SessionService sessionService;

    /**
     * 查询会话列表（按更新时间倒序）
     */
    @GetMapping
    public Result<List<SessionVO>> listSession() {
        return Result.success(sessionService.listSession());
    }

    /**
     * 更新会话标题
     * @return 处理结果
     */
    @PutMapping("/{sessionId}")
    public Result<SessionVO> updateTitle(@Valid @RequestBody SessionDTO sessionDTO) {
        return Result.success(sessionService.updateTitle(sessionDTO));
    }

    /**
     * 删除会话
     */
    @DeleteMapping
    public Result<Boolean> deleteSession(@RequestBody Long  sessionId) {
        return Result.success(sessionService.deleteSession(sessionId));
    }

}
