package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/29
 */
@Controller
@RequestMapping("/api")
public class SessionsController {
    private final ChatService chatService;

    @Autowired
    public SessionsController(ChatService chatService) {this.chatService = chatService;}

    @PostMapping("/sessions")
    public ResponseEntity<Object> addSession(@RequestParam("client_id") int clientId, @RequestParam("consultant_id") int consultantId, @RequestParam(value = "schedule_id", required = false) Integer scheduleId) {
        return chatService.addSession(clientId, consultantId, scheduleId);
    }
}
