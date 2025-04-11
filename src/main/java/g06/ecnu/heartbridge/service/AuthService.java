package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Admin;
import g06.ecnu.heartbridge.entity.ConsultantDetail;
import g06.ecnu.heartbridge.entity.Users;
import g06.ecnu.heartbridge.mapper.AdminMapper;
import g06.ecnu.heartbridge.mapper.ConsultantDetailMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import g06.ecnu.heartbridge.utils.PatternValidator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import g06.ecnu.heartbridge.utils.JwtUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Service
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
    @Autowired
    private ConsultantDetailMapper consultantDetailMapper;

    //注册
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> register(String username, String password, String phone, String email, String auth_code) {
        try {
            if (!PatternValidator.validatePattern(username).equals("USERNAME")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名不合法\"}");
            }
            if (!PatternValidator.validatePattern(phone).equals("PHONE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"电话不合法\"}");
            }
            if (email != null && !email.isEmpty() && !PatternValidator.validatePattern(email).equals("EMAIL")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"邮箱不合法\"}");
            }
            if (auth_code == null || auth_code.isEmpty()) {
                if (ifUserExists(username, phone, email, "client")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或电话或邮箱已存在\"}");
                } else {
                    String userType = "client";
                    Users user = new Users(username, password, phone, email, userType);
                    int result = usersMapper.insert(user);
                    int userId = user.getId();
                    String token = JwtUtil.generateToken(username, userType, userId);
                    Map<String, String> response = new HashMap<>();
                    response.put("id", String.valueOf(userId));
                    response.put("username", username);
                    response.put("type", "0");
                    response.put("token", token);

                    if (result > 0) {
                        return ResponseEntity.ok(response);
                    } else {
                        throw new RuntimeException("insert consultant failed");
                    }
                }
            } else {
                if (ifUserExists(username, phone, email, "consultant")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或电话或邮箱已存在\"}");
                } else {
                    String userType = "consultant";
                    Users user = new Users(username, password, phone, email, userType);
                    int userInsertResult = usersMapper.insert(user);
                    ConsultantDetail consultantDetail = new ConsultantDetail();
                    consultantDetail.setUserId(user.getId());
                    consultantDetail.setCertification(auth_code);
                    int consultantDetailInsertResult = consultantDetailMapper.insert(consultantDetail);

                    int userId = user.getId();
                    String token = JwtUtil.generateToken(username, userType, userId);
                    Map<String, String> response = new HashMap<>();
                    response.put("id", String.valueOf(userId));
                    response.put("username", username);
                    response.put("type", "1");
                    response.put("token", token);

                    if (userInsertResult > 0 && consultantDetailInsertResult > 0) {
                        return ResponseEntity.ok(response);
                    } else {
                        throw new RuntimeException("insert consultant failed");
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }


    //登录
    public ResponseEntity<Object> login(String loginParam, String password, int role) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (role == 0)
            queryWrapper.eq("type", "consultant");
        else if (role == 1)
            queryWrapper.eq("type", "client");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户身份无效\"}");

        switch (PatternValidator.validatePattern(loginParam)) {
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
        Users user = usersMapper.selectOne(queryWrapper);
        if (user != null && user.getPassword().equals(password)) {
            if (user.getStatus().equals("inactive"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"您已被管理员封禁\"}");
            if (role == 0) {
                QueryWrapper<ConsultantDetail> consultantDetailQueryWrapper = new QueryWrapper<>();
                consultantDetailQueryWrapper.eq("user_id", user.getId());
                ConsultantDetail consultantDetail = consultantDetailMapper.selectOne(consultantDetailQueryWrapper);
                if (consultantDetail.getCertification().equals("no")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"您的资格审查尚未通过，无法登录\"}");
                }
            }
            String token = JwtUtil.generateToken(user.getUsername(), user.getType(), user.getId());
            Map<String, String> response = new HashMap<>();
            response.put("id", String.valueOf(user.getId()));
            response.put("username", user.getUsername());
            response.put("type", user.getType());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或密码错误\"}");
        }
    }


    //管理员登录
    public ResponseEntity<Object> loginAdmin(String id, String password) {
        Admin admin = adminMapper.selectById(id);
        if (admin != null && admin.getPassword().equals(password)) {
            String token = JwtUtil.generateToken(String.valueOf(admin.getId()), "admin", admin.getId());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"用户名或密码错误\"}");
        }
    }

    private boolean ifUserExists(String username, String phone, String email, String type) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                .eq("type", type)
                .and(wrapper -> wrapper
                        .eq("phone", phone)
                        .or().eq("email", email));
        return usersMapper.selectCount(queryWrapper) > 0;
    }
}
