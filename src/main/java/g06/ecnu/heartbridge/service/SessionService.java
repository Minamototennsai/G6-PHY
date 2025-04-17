package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.DTO.MessageHistoryDTO;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Object> getSessions(int userId, String userType) {
        QueryWrapper<UserSession> queryWrapper = new QueryWrapper<>();
        if (userType.equals("0")) {
            queryWrapper.eq("consultant_id", userId);
        } else {
            queryWrapper.eq("client_id", userId);
        }
        List<UserSession> userSessions = userSessionMapper.selectList(queryWrapper);
        Set<Sessions> sessionsSet = new HashSet<>();
        if (!userSessions.isEmpty()) {
            for (UserSession userSession : userSessions) {
                sessionsSet.add(sessionsMapper.selectById(userSession.getSessionId()));
            }
            Map<Sessions, Set<Integer>> map = new HashMap<>();
            for (Sessions session : sessionsSet) {
                if (!map.containsKey(session)) {
                    map.put(session, new HashSet<>());
                }
                QueryWrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<>();
                userSessionQueryWrapper.eq("session_id", session.getId());
                List<UserSession> userSessionList = userSessionMapper.selectList(userSessionQueryWrapper);
                for (UserSession userSession : userSessionList) {
                    map.get(session).add(userSession.getClientId());
                    map.get(session).add(userSession.getConsultantId());
                }
            }
            ObjectNode response = objectMapper.createObjectNode();
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (Sessions session : map.keySet()) {
                ObjectNode sessionNode = objectMapper.createObjectNode();
                sessionNode.put("session_id", session.getId());
                ArrayNode usernameNode = objectMapper.createArrayNode();
                for (int id : map.get(session)) {
                    usernameNode.add(usersMapper.selectById(id).getUsername());
                }
                sessionNode.set("username", usernameNode);
                ArrayNode userNodes = objectMapper.createArrayNode();
                for (int id : map.get(session)) {
                    ObjectNode userNode = objectMapper.createObjectNode();
                    userNode.put("id", id);
                    userNode.put("username", usersMapper.selectById(id).getUsername());
                    userNodes.add(userNode);
                }
                sessionNode.set("user", userNodes);
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
