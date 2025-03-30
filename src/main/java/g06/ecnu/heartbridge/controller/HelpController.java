package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.HelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<Object> addHelp(@RequestParam int consultantId, @RequestParam int sessionId, @RequestParam String content){
        return helpService.addHelp(consultantId, sessionId, content);
    }
}
