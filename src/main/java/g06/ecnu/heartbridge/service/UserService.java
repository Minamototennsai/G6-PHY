package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.Users;
import g06.ecnu.heartbridge.mapper.TagMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import g06.ecnu.heartbridge.pojo.ChangePasswordRequestBody;
import g06.ecnu.heartbridge.pojo.PatchUserInfoRequestBody;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/9
 */
@Service
public class UserService {
    @Resource
    private UsersMapper usersMapper;
    @Resource
    private TagMapper tagMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Object> getUserInfo(int id){
        QueryWrapper<Users> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", id);
        Users user = usersMapper.selectOne(userQueryWrapper);
        if (user != null) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("password", "*");
            data.put("phone", user.getPhone());
            data.put("type", user.getType());
            data.put("profile", user.getProfile());
            data.put("theme_preference", user.getThemePreference());
            ArrayNode preference = objectMapper.createArrayNode();
            List<String> preferenceTags = tagMapper.getClientTags(id);
            for (String tag : preferenceTags) {
                preference.add(tag);
            }
            data.set("preference", preference);
            if (user.getType().equals("consultant")) {
                ArrayNode expertise = objectMapper.createArrayNode();
                List<String> expertiseTags = tagMapper.getConsultantTags(id);
                for (String tag : expertiseTags) {
                    expertise.add(tag);
                }
                data.set("expertise", expertise);
            }
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到用户\"}");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> updateUserInfo(int id, PatchUserInfoRequestBody body){
        try {
            UpdateWrapper<Users> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("id", id)
                    .set("username", body.getUsername())
                    .set("email", body.getEmail())
                    .set("phone", body.getPhone())
                    .set("profile", body.getProfile())
                    .set("theme_preference", body.getTheme_preference());
            int result = usersMapper.update(userUpdateWrapper);
            if (body.getPreference() != null) {
                tagMapper.deletePreferenceTagByUserId(id);
                for (Integer tagId : body.getPreference()) {
                    tagMapper.insertPreferenceTag(id, tagId);
                }
            }
            if (body.getExpertise() != null) {
                tagMapper.deleteExpertiseTagByUserId(id);
                for (Integer tagId : body.getExpertise()) {
                    tagMapper.insertExpertiseTag(id, tagId);
                }
            }
            if (result == 1) {
                return ResponseEntity.ok().body("{\"message\":\"修改成功\"}");
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"修改失败\"}");
        }
    }

    public ResponseEntity<Object> changePassword(int id, ChangePasswordRequestBody body){
        UpdateWrapper<Users> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", id)
                .eq("password", body.getCurrent_password())
                .set("password", body.getNew_password());
        int result = usersMapper.update(userUpdateWrapper);
        if (result == 1) {
            return ResponseEntity.ok().body("{\"message\":\"修改成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"旧密码错误\"}");
        }
    }
}
