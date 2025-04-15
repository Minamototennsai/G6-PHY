package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.TagService;
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
 * @since 2025/4/10
 */
@Controller
@RequestMapping("/api")
public class TagController {
    private final TagService tagService;
    @Autowired
    public TagController(TagService tagService) {this.tagService = tagService;}

    @GetMapping("/tags")
    public ResponseEntity<Object> getTags() {
        return tagService.getTags();
    }
}
