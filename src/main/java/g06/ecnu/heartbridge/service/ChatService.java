package g06.ecnu.heartbridge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import g06.ecnu.heartbridge.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<String, WebSocketSession> sessionMapNameToSession = new ConcurrentHashMap<>();
    private Map<WebSocketSession, String> sessionMapSessionToName = new ConcurrentHashMap<>();

    public ChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void onConnect(WebSocketSession session){
        try {
            String path = session.getUri().getPath();
            String token = path.substring(path.lastIndexOf("/") + 1);
            String username = JwtUtil.validateToken(token).getSubject();
            sessionMapNameToSession.put(username, session);
            sessionMapSessionToName.put(session, username);
        } catch (Exception e) {
            closeSession(session);
        }
    }

    public void onMessage(WebSocketSession session, String message){
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String dest = jsonNode.get("to").asText();
            WebSocketSession destSession = sessionMapNameToSession.get(dest);
            destSession.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClose(WebSocketSession session){
        String username = sessionMapSessionToName.get(session);
        sessionMapNameToSession.remove(username);
        sessionMapSessionToName.remove(session);
    }

    public void closeSession(WebSocketSession session){
        try {
            session.close(CloseStatus.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
