package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.DTO.MessageHistoryDTO;
import g06.ecnu.heartbridge.DTO.SessionListDTO;
import g06.ecnu.heartbridge.entity.Sessions;
import g06.ecnu.heartbridge.entity.UserSession;
import g06.ecnu.heartbridge.mapper.ChatMessageMapper;
import g06.ecnu.heartbridge.mapper.SessionsMapper;
import g06.ecnu.heartbridge.mapper.UserSessionMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private UserSessionMapper userSessionMapper;
    @Resource
    private SessionsMapper sessionsMapper;
    @Resource
    private UsersMapper usersMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Object> getSessions(int userId, String userType) {
//        Map<String, Object> response = new HashMap<>();
//        List<SessionListDTO> data = sessionsMapper.getSessionsByUserId(userId, userType);
//        if (!data.isEmpty()) {
//            response.put("data", data);
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("message", "未获取到相关会话");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
        QueryWrapper<UserSession> queryWrapper = new QueryWrapper();
        if (userType.equals("0")) {
            queryWrapper.eq("consultant_id", userId);
            System.out.printf("consultant_id: %s\n", userId);
        } else {
            queryWrapper.eq("client_id", userId);
        }
        List<UserSession> userSessions = userSessionMapper.selectList(queryWrapper);
        if (!userSessions.isEmpty()) {
            Map<Sessions, Set<String>> map = new HashMap<>();
            for (UserSession userSession : userSessions) {
                Sessions session = sessionsMapper.selectById(userSession.getSessionId());
                if (session != null) {
                    if (!map.containsKey(session)) {
                        map.put(session, new HashSet<>());
                    }
                    map.get(session).add(usersMapper.selectById(userSession.getClientId()).getUsername());
                    map.get(session).add(usersMapper.selectById(userSession.getConsultantId()).getUsername());
                }
            }
            ObjectNode response = objectMapper.createObjectNode();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (Sessions session : map.keySet()) {
                ObjectNode sessionNode = objectMapper.createObjectNode();
                sessionNode.put("session_id", session.getId());
                ArrayNode usernameNode = objectMapper.createArrayNode();
                for (String username : map.get(session)) {
                    usernameNode.add(username);
                }
                sessionNode.set("username", usernameNode);
                if (session.getEndTime() != null) {
                    sessionNode.put("end_time", session.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } else {
                    sessionNode.put("end_time", "");
                }
                arrayNode.add(sessionNode);
            }
            response.set("data", arrayNode);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未获取到相关会话\"}");
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
