package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.HelpService;
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
 * @since 2025/3/24
 */

@Controller
@RequestMapping("/api")
public class HelpController {
    private final HelpService helpService;

    @Autowired
    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    @GetMapping("/help")
    public ResponseEntity<Object> getHelp(@RequestParam(required = false) Integer helpId){
        return helpService.getHelp(helpId);
    }

    @PostMapping("/help")
    public ResponseEntity<Object> addHelp(@RequestParam String help_id, @RequestParam int consultantId, @RequestParam int sessionId, @RequestParam String content){
        return helpService.addHelp(help_id, consultantId, sessionId, content);
    }

    @PostMapping("/help/{help_id}")
    public ResponseEntity<Object> handleHelp(@PathVariable int help_id, @RequestAttribute("userId") int consultantId){
        return helpService.handleHelp(help_id, consultantId);
    }
}
