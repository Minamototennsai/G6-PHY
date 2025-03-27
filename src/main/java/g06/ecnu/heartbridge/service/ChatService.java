package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Queue;

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
    private Map<WebSocketSession, String> sessionMap;
    private Queue<String> offlineMessageQueue;
    public void onConnect(WebSocketSession session){
        try {
            String path = session.getUri().getPath();
            String token = path.substring(path.lastIndexOf("/") + 1);
            String username = JwtUtil.validateToken(token).getSubject();
            sessionMap.put(session, username);
        } catch (Exception e) {
            closeSession(session);
        }
    }

    public void onMessage(WebSocketSession session, String message){

    }

    public void onClose(WebSocketSession session){
        sessionMap.remove(session);
    }

    public void closeSession(WebSocketSession session){
        try {
            session.close(CloseStatus.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
