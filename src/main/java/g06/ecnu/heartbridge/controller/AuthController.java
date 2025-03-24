package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 *  Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-12
 */
@Controller
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<Object> register(@RequestParam String username,@RequestParam String phone, @RequestParam(required = false) String email, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam int agree){
        return authService.register(username, password, phone, email, confirmPassword, agree);
    }

    @PostMapping("/user/login")
    public ResponseEntity<Object> login(@RequestParam String id, @RequestParam String password){
        return authService.login(id, password);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Object> loginAdmin(@RequestParam String id, @RequestParam String password){
        return authService.loginAdmin(id, password);
    }
}
