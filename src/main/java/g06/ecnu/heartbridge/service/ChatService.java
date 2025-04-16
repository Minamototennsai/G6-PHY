package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.*;
import g06.ecnu.heartbridge.mapper.*;
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
import java.util.*;
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

    @Resource
    private ConsultantDetailMapper consultantDetailMapper;

    @Resource
    private UsersMapper usersMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Integer, CopyOnWriteArraySet<Integer>> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, WebSocketSession> sessionMapIdToSession = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Integer> sessionMapSessionToId = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Lock> sessionLocks = new ConcurrentHashMap<>();

    public void onConnect(WebSocketSession webSocketSession){
        System.out.println(webSocketSession.getUri().getPath());
        try {
            String path = webSocketSession.getUri().getPath();
            String token = path.substring(path.lastIndexOf("/") + 1);
            System.out.println(token);
            System.out.println(JwtUtil.validateToken(token).getSubject());
            int userId = JwtUtil.validateToken(token).get("userId", Integer.class);
            System.out.println(userId);
            sessionMapIdToSession.put(userId, webSocketSession);
            sessionMapSessionToId.put(webSocketSession, userId);
        } catch (Exception e) {
            closeWebSocketSession(webSocketSession);
        }
    }

    public void onMessage(WebSocketSession sourceWebSocketSession, String message){
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            int senderId = sessionMapSessionToId.get(sourceWebSocketSession);
            int destSession = jsonNode.get("to").asInt();
            boolean toShow = jsonNode.get("toShow").asBoolean();
            QueryWrapper<Sessions> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", destSession);
            Long ifSessionExist = sessionsMapper.selectCount(queryWrapper);
            if (ifSessionExist == 0) {
                throw new NullPointerException();
            }
            List<WebSocketSession> webSocketSessions = sessions.get(destSession)
                    .stream()
                    .map(sessionMapIdToSession::get)
                    .filter(Objects::nonNull)
                    .toList();
            if (!toShow) {
                for (WebSocketSession destWebSocketSession : webSocketSessions) {
                    if (!usersMapper.selectById(sessionMapSessionToId.get(destWebSocketSession)).getType().equals("client")) {
                        sendMessage(destWebSocketSession, message);
                    }
                }
                return;
            }
            for (WebSocketSession destWebSocketSession : webSocketSessions) {
                sendMessage(destWebSocketSession, message);
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(senderId);
            chatMessage.setSessionId(destSession);
            chatMessage.setContent(jsonNode.get("text").asText());
            chatMessage.setSendTime(LocalDateTime.now());
            chatMessageMapper.insert(chatMessage);
        } catch (JsonProcessingException e) {
            sendMessage(sourceWebSocketSession, "{\"error\":\"消息解析失败\"}");
        } catch (NullPointerException e) {
            sendMessage(sourceWebSocketSession, "{\"error\":\"目标会话不存在或已结束\"}");
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

    public void closeWebSocketSession(WebSocketSession webSocketSession){
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
        updateWrapper.eq("id", sessionId)
                .set("is_overtime", "yes")
                .set("end_time", LocalDateTime.now());
        int result = sessionsMapper.update(updateWrapper);
        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"离开会话失败\"}");
        } else {
            sessions.remove(sessionId);
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"咨询已结束\"}");
        }
    }


    public ResponseEntity<Object> evaluate(int sessionId, int consultantId, int score){
        QueryWrapper<ConsultantDetail> consultantDetailQueryWrapper = new QueryWrapper<>();
        QueryWrapper<UserSession> userSessionQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Sessions> sessionQueryWrapper = new QueryWrapper<>();
        sessionQueryWrapper.eq("id", sessionId);
        Sessions session = sessionsMapper.selectOne(sessionQueryWrapper);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"会话不存在\"}");
        } else if (session.getIsOvertime().equals("no")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"咨询尚未结束，无法评价\"}");
        }
        userSessionQueryWrapper.eq("session_id", sessionId)
                        .eq("consultant_id", consultantId);
        UserSession userSession = userSessionMapper.selectOne(userSessionQueryWrapper);
        if (userSession == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"咨询师id输入错误\"}");
        } else if (userSession.getIsEvaluated() == 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"已经评价过，请勿重复评价\"}");
        }
        consultantDetailQueryWrapper.eq("user_id", consultantId);
        UpdateWrapper<ConsultantDetail> consultantDetailUpdateWrapper = new UpdateWrapper<>();
        ConsultantDetail consultantDetail = consultantDetailMapper.selectOne(consultantDetailQueryWrapper);
        double currentScore = consultantDetail.getAvgScore();
        int currentEvaluationCount = consultantDetail.getEvaluationCount();
        consultantDetailUpdateWrapper.eq("user_id", consultantId)
                .set("avg_score", (currentScore * currentEvaluationCount + score)/(currentScore+1))
                .set("evaluation_count", currentEvaluationCount + 1);
        int result = consultantDetailMapper.update(consultantDetailUpdateWrapper);
        if(result == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库错误，请稍后再试\"}");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"评价成功\"}");
        }
    }

    public int joinSession(int consultantId, int sessionId){
        QueryWrapper<UserSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", sessionId);
        UserSession userSession = userSessionMapper.selectOne(queryWrapper);
        if(userSession != null){
            int clientId = userSession.getClientId();
            UserSession newUserSession = new UserSession();
            newUserSession.setConsultantId(consultantId);
            newUserSession.setSessionId(sessionId);
            newUserSession.setClientId(clientId);
            int result = userSessionMapper.insert(newUserSession);
            if(result == 1){
                sessions.get(sessionId).add(consultantId);
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public boolean ifUserInSession(int userId){
        for (CopyOnWriteArraySet<Integer> list : sessions.values()) {
            if (list.contains(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean ifUserOnline(int userId){
        return sessionMapIdToSession.containsKey(userId);
    }

    public int getDaily() {
        return sessionMapIdToSession.size();
    }

    public List<Integer> getCurrentSessions(){
        return new ArrayList<>(sessions.keySet());
    }
}
