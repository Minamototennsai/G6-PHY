package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.pojo.PostForumRequestBody;
import g06.ecnu.heartbridge.service.ForumService;
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
 * @since 2025/4/10
 */
@Controller
@RequestMapping("/api")
public class ForumController {
    private final ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/forums")
    public ResponseEntity<Object> getForums(@RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false) String keyword, @RequestParam(required = false) String tags) {
        return forumService.getForums(page, keyword, tags);
    }

    @PostMapping("/forums")
    public ResponseEntity<Object> addForums(@RequestAttribute("userId") int userId, @RequestBody PostForumRequestBody body) {
        return forumService.addForum(userId, body);
    }

    @GetMapping("/forums/{id}")
    public ResponseEntity<Object> getForum(@PathVariable int id) {
        return forumService.getForum(id);
    }

    @GetMapping("/forums/join")
    public ResponseEntity<Object> getJoinedForums(@RequestAttribute("userId") int userId, @RequestParam(required = false, defaultValue = "1") int page) {
        return forumService.getJoinedForums(userId, page);
    }

    @GetMapping("/forums/create")
    public ResponseEntity<Object> getPostedForums(@RequestAttribute("userId") int userId, @RequestParam(required = false, defaultValue = "1") int page) {
        return forumService.getPostedForums(userId, page);
    }

    @DeleteMapping("/forums/{id}")
    public ResponseEntity<Object> deleteForum(@PathVariable int id, @RequestAttribute("userType") String userType, @RequestAttribute("userId") int userId) {
        return forumService.deleteForum(id, userType, userId);
    }
}
