package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Admin;
import g06.ecnu.heartbridge.entity.Users;
import g06.ecnu.heartbridge.mapper.AdminMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import g06.ecnu.heartbridge.utils.PatternValidator;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import g06.ecnu.heartbridge.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/12
 */
@Service
public class AuthService {
    @Resource
    private UsersMapper usersMapper;
    @Resource
    private AdminMapper adminMapper;

    //注册
    public ResponseEntity<Object> register(String username, String password, String phone, String email, String confirmPassword, int agree){
        if (agree == 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"请阅读并同意相关协议\"}");
        } else if (!password.equals(confirmPassword)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"两次密码不一致\"}");
        } else {
            if (!PatternValidator.validatePattern(username).equals("USERNAME")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名不合法\"}");
            }
            if (!PatternValidator.validatePattern(phone).equals("PHONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"电话不合法\"}");
            }
            if (email == null || email.isEmpty()){
                if (!PatternValidator.validatePattern(email).equals("EMAIL")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"邮箱不合法\"}");
                }
            }
            if (ifUserExists(username, phone, email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或电话或邮箱已存在\"}");
            } else {
                String userType = "client";
                Users user = new Users(username, password, phone, email, userType);
                int result = usersMapper.insert(user);
                int userId = user.getId();
                String token = JwtUtil.generateToken(username, userType, userId);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);

                return result > 0 ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库错误，请重试\"}");
            }
        }
    }

    //登录
    public ResponseEntity<Object> login(String loginParam, String password){
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();

        switch (PatternValidator.validatePattern(loginParam)){
            case "EMAIL":
                queryWrapper.eq("email", loginParam);
                break;
            case "PHONE":
                queryWrapper.eq("phone", loginParam);
                break;
            case "USERNAME":
                queryWrapper.eq("username", loginParam);
                break;
            case "INVALID":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"请输入有效的邮箱或用户名或电话号\"}");
        }
        Users users = usersMapper.selectOne(queryWrapper);
        if (users != null && users.getPassword().equals(password)) {
            String token = JwtUtil.generateToken(users.getUsername(), users.getType(), users.getId());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", users.getUsername());
            response.put("id", String.valueOf(users.getId()));
            response.put("type", String.valueOf(users.getType()));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或密码错误\"}");
        }
    }


    //管理员登录
    public ResponseEntity<Object> loginAdmin(String id, String password){
        Admin admin = adminMapper.selectById(id);
        if (admin != null && admin.getPassword().equals(password)) {
            String token = JwtUtil.generateToken(String.valueOf(admin.getId()), "admin", admin.getId());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "2");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或密码错误\"}");
        }
    }

    private boolean ifUserExists(String username, String phone, String email) {
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .or().eq("phone", phone)
                .or().eq("email", email);
        return usersMapper.selectCount(wrapper) > 0;
    }
}
