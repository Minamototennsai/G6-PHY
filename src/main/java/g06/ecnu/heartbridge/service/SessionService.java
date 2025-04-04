package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.MessageHistoryDTO;
import g06.ecnu.heartbridge.DTO.SessionListDTO;
import g06.ecnu.heartbridge.mapper.ChatMessageMapper;
import g06.ecnu.heartbridge.mapper.SessionsMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
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

    @Resource
    private SessionsMapper sessionsMapper;

    public ResponseEntity<Object> getSessions(int userId, String userType) {
        Map<String, Object> response = new HashMap<>();
        List<SessionListDTO> data = sessionsMapper.getSessionsByUserId(userId, userType);
        if (!data.isEmpty()) {
            response.put("data", data);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "未获取到相关会话");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    public ResponseEntity<Object> getMessages(int sessionId) {
        Map<String, Object> response = new HashMap<>();
        List<MessageHistoryDTO> data = chatMessageMapper.getMessagesBySessionId(sessionId);
        if (!data.isEmpty()) {
            response.put("data", data);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "未获取到聊天记录");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }
}
