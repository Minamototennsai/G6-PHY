package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.NewManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/16
 */
@Controller
@RequestMapping("/api")
public class NewManagerController {
    private final NewManagerService newManagerService;

    @Autowired
    public NewManagerController(NewManagerService newManagerService) {
        this.newManagerService = newManagerService;
    }

    @GetMapping("/admin/daily")
    public ResponseEntity<Object> getDaily(){
        return newManagerService.getDaily();
    }

    @GetMapping("/scorerank")
    public ResponseEntity<Object> getScorerank(){
        return newManagerService.getScorerank();
    }

    @GetMapping("/sessionrank")
    public ResponseEntity<Object> getSessionRank(){
        return newManagerService.getSessionRank();
    }

    @GetMapping("/nowsession")
    public ResponseEntity<Object> getNowSession(){
        return newManagerService.getNowSession();
    }

    @GetMapping("/schedule/week")
    public ResponseEntity<Object> getScheduleWeek(){
        return newManagerService.getScheduleWeek();
    }
}
