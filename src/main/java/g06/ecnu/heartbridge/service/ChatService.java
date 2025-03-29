package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.ChatMessage;
import g06.ecnu.heartbridge.entity.Sessions;
import g06.ecnu.heartbridge.entity.UserSession;
import g06.ecnu.heartbridge.mapper.ChatMessageMapper;
import g06.ecnu.heartbridge.mapper.SessionsMapper;
import g06.ecnu.heartbridge.mapper.UserSessionMapper;
import g06.ecnu.heartbridge.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/27
 */
@Service
public class ChatService {
    @Resource
    private SessionsMapper sessionsMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    private final ObjectMapper objectMapper;
    private final Map<Integer, CopyOnWriteArraySet<Integer>> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, WebSocketSession> sessionMapIdToSession = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Integer> sessionMapSessionToId = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Lock> sessionLocks = new ConcurrentHashMap<>();

    public ChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void onConnect(WebSocketSession webSocketSession){
        try {
            String path = webSocketSession.getUri().getPath();
            String token = path.substring(path.lastIndexOf("/") + 1);
            int userId = Integer.parseInt(JwtUtil.validateToken(token).get("userId", String.class));
            sessionMapIdToSession.put(userId, webSocketSession);
            sessionMapSessionToId.put(webSocketSession, userId);
        } catch (Exception e) {
            closeSession(webSocketSession);
        }
    }

    public void onMessage(WebSocketSession sourceWebSocketSession, String message){
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            int senderId = sessionMapSessionToId.get(sourceWebSocketSession);
            int destSession = jsonNode.get("to").asInt();
            QueryWrapper<Sessions> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", destSession);
            Long ifSessionExist = sessionsMapper.selectCount(queryWrapper);
            if (ifSessionExist == 0){
                throw new NullPointerException();
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(senderId);
            chatMessage.setSessionId(destSession);
            chatMessage.setContent(jsonNode.get("text").asText());
            chatMessage.setSendTime(LocalDateTime.now());
            chatMessageMapper.insert(chatMessage);
            List<WebSocketSession> webSocketSessions = sessions.get(destSession)
                    .stream()
                    .map(sessionMapIdToSession::get)
                    .filter(Objects::nonNull)
                    .toList();
            for (WebSocketSession destWebSocketSession : webSocketSessions) {
                sendMessage(destWebSocketSession, message);
            }
        } catch (JsonProcessingException e) {
            sendMessage(sourceWebSocketSession, "{\"error\":\"消息解析失败\"}");
        } catch (NullPointerException e) {
            sendMessage(sourceWebSocketSession, "{\"error\":\"目标会话不存在\"}");
        }
    }

    private void sendMessage(WebSocketSession destSession, String message) {
        Lock lock = sessionLocks.computeIfAbsent(destSession, k -> new ReentrantLock());
        lock.lock();
        try {
            destSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void onClose(WebSocketSession webSocketSession){
        synchronized (this) {
            try {
                int userId = sessionMapSessionToId.get(webSocketSession);
                sessionMapSessionToId.remove(webSocketSession);
                sessionMapIdToSession.remove(userId);
            } catch (Exception ignored) {}
        }
    }

    public void closeSession(WebSocketSession webSocketSession){
        try {
            webSocketSession.close(CloseStatus.NORMAL);
        } catch (Exception ignored) {}
    }

    public ResponseEntity<Object> addSession(int clientId, int consultantId, Integer schedule_id){
        Sessions session = new Sessions();
        session.setScheduleId(schedule_id);
        session.setStartTime(LocalDateTime.now());
        session.setIsOvertime("no");
        int result = sessionsMapper.insert(session);
        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"创建会话失败\"}");
        }
        UserSession userSession = new UserSession();
        userSession.setClientId(clientId);
        userSession.setConsultantId(consultantId);
        userSession.setSessionId(session.getId());
        result = userSessionMapper.insert(userSession);
        if(result == 0){
            sessionsMapper.deleteById(session.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"创建会话失败\"}");
        }
        sessions.put(session.getId(), new CopyOnWriteArraySet<>());
        sessions.get(session.getId()).add(clientId);
        sessions.get(session.getId()).add(consultantId);
        ObjectNode response = objectMapper.createObjectNode();
        ObjectNode data = objectMapper.createObjectNode();
        data.put("session_id", session.getId());
        data.put("start_time", LocalDateTime
                .now()
                .format(DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.set("data", data);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<Object> closeSession(int sessionId){
        UpdateWrapper<Sessions> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("session_id", sessionId)
                .set("isOvertime", "yes")
                .set("end_time", LocalDateTime.now());
        int result = sessionsMapper.update(updateWrapper);
        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"离开会话失败\"}");
        } else {
            sessions.remove(sessionId);
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"咨询已结束\"}");
        }
    }
}
