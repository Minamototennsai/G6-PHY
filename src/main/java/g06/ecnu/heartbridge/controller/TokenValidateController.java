package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/15
 */
@Controller
@RequestMapping("/api")
public class TokenValidateController {
    @GetMapping("/token")
    public ResponseEntity<Object> validateToken(@RequestParam("token") String token) {
        Claims claim = JwtUtil.validateToken(token);
        if (claim != null) {
            return ResponseEntity.ok().body("{\"isCorrect\": true}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"isCorrect\": false}");
        }
    }
}
