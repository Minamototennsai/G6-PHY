package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * <p>
 *  用于处理websocket的控制器
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/19
 */

@Component
public class ChatWebSocketController extends TextWebSocketHandler {
    private final ChatService chatService;

    @Autowired
    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        chatService.onConnect(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        chatService.onMessage(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        chatService.onClose(session);
    }
}
