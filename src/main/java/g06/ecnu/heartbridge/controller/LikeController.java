package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
public class LikeController {
    private final LikeService likeService;
    @Autowired
    public LikeController(LikeService likeService) {this.likeService = likeService;}

    @PostMapping("/likes")
    public ResponseEntity<Object> changeLike(@RequestAttribute("userId") int userId, @RequestParam String type, @RequestParam int target_id) {
        return likeService.changeLike(userId, type, target_id);
    }
}
