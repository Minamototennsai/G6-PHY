package g06.ecnu.heartbridge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import g06.ecnu.heartbridge.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
    private final ObjectMapper objectMapper;
    private final Map<Integer, CopyOnWriteArrayList<Integer>> sessions = new ConcurrentHashMap<>();
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
            int destSession = Integer.parseInt(jsonNode.get("to").asText());
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
        return null;
    }

    public ResponseEntity<Object> closeSession(int sessionId){
        return null;
    }
}
