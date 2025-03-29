package g06.ecnu.heartbridge.config;

import g06.ecnu.heartbridge.controller.ChatWebSocketController;
import g06.ecnu.heartbridge.service.ChatService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * <p>
 * WebSocket配置类
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/29
 */
@Configuration
@EnableWebSocket // 启用 WebSocket 支持
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketController chatWebSocketController;

    public WebSocketConfig(ChatWebSocketController chatWebSocketController) {this.chatWebSocketController = chatWebSocketController;}

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketController, "/chat")
                .setAllowedOrigins("*"); // 允许所有来源

    }
}
