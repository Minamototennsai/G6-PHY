package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.ChatService;
import g06.ecnu.heartbridge.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
public class SessionController {
    private final ChatService chatService;
    private final SessionService sessionService;

    @Autowired
    public SessionController(ChatService chatService, SessionService sessionService) {
        this.chatService = chatService;
        this.sessionService = sessionService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<Object> addSession(@RequestParam("client_id") int clientId, @RequestParam("consultant_id") int consultantId, @RequestParam(value = "schedule_id", required = false) Integer scheduleId) {
        return chatService.addSession(clientId, consultantId, scheduleId);
    }

    @PostMapping("/sessions/{session_id}/end")
    public ResponseEntity<Object> closeSession(@PathVariable("session_id") int sessionId) {
        return chatService.closeSession(sessionId);
    }

    @GetMapping("/sessions")
    public ResponseEntity<Object> getSessions(@RequestParam("user_id") int userId, @RequestParam("user_type") String userType) {
        return sessionService.getSessions(userId, userType);
    }

    @GetMapping("/sessions/{session_id}/messages")
    public ResponseEntity<Object> getMessages(@PathVariable("session_id") int sessionId) {
        return sessionService.getMessages(sessionId);
    }

    @PostMapping("/sessions/{session_id}/evaluation")
    public ResponseEntity<Object> evaluate(@PathVariable("session_id") int sessionId, @RequestParam int consultant_id, @RequestParam int score) {
        return chatService.evaluate(sessionId, consultant_id, score);
    }
}
