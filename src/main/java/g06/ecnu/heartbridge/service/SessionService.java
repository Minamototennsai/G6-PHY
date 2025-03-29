package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.ChatMessage;
import g06.ecnu.heartbridge.mapper.ChatMessageMapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/30
 */
@Service
public class SessionService {
    @Resource
    private ChatMessageMapper chatMessageMapper;

    public ResponseEntity<Object> getSessions(int userId, String userType) {
        //TODO: 会话详情
        return null;
    }

    public ResponseEntity<Object> getMessages(int sessionId) {
        //TODO: 修改为DTO，现在与接口不匹配
        QueryWrapper<ChatMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", sessionId);
        List<ChatMessage> data = chatMessageMapper.selectList(queryWrapper);
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
