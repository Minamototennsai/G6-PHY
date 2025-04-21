package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.pojo.ChangePasswordRequestBody;
import g06.ecnu.heartbridge.pojo.PatchUserInfoRequestBody;
import g06.ecnu.heartbridge.service.UserService;
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
 * @since 2025/4/9
 */
@Controller
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserInfo(@PathVariable int id){
        return userService.getUserInfo(id);
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<Object> updateUserInfo(@PathVariable int id, @RequestBody PatchUserInfoRequestBody body){
        return userService.updateUserInfo(id, body);
    }

    @PostMapping("/users/{id}/password")
    public ResponseEntity<Object> changePassword(@PathVariable int id, @RequestBody ChangePasswordRequestBody body){
        return userService.changePassword(id, body);
    }
}
