package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.pojo.CommentRequestBody;
import g06.ecnu.heartbridge.service.CommentService;
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
 * @since 2025/4/8
 */
@Controller
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    public ResponseEntity<Object> addComment(@RequestBody CommentRequestBody commentRequestBody, @RequestAttribute("userId") int userId) {
        return commentService.addComment(commentRequestBody.getType(), commentRequestBody.getTarget_id(), commentRequestBody.getContent(), userId);
    }

    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<Object> deleteComment(@PathVariable int comment_id, @RequestAttribute("userType") String userType, @RequestAttribute("userId") int userId) {
        return commentService.deleteComment(comment_id, userType, userId);
    }

    @GetMapping("/articles/{article_id}/comments")
    public ResponseEntity<Object> getArticleComments(@PathVariable int article_id, @RequestAttribute("userId") int userId, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return commentService.getArticleComments(article_id, userId, page, pageSize);
    }

    @GetMapping("/forums/{forums_id}/comments")
    public ResponseEntity<Object> getForumComments(@PathVariable int forums_id, @RequestAttribute("userId") int userId, @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return commentService.getForumComments(forums_id, userId, page, pageSize);
    }
}
