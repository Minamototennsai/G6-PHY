package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Liked;
import g06.ecnu.heartbridge.mapper.LikedMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Service
public class LikeService {
    @Resource
    private LikedMapper likedMapper;

    public ResponseEntity<Object> changeLike(int userId, String type, int targetId) {
        QueryWrapper<Liked> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("type", type)
                .eq("target_id", targetId);
        Liked liked = likedMapper.selectOne(queryWrapper);
        if (liked == null) {
            Liked like = new Liked();
            like.setUserId(userId);
            like.setType(type);
            like.setTargetId(targetId);
            int result = likedMapper.insert(like);
            if (result == 1) {
                return ResponseEntity.ok("{\"message\":\"点赞成功\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"点赞失败\"}");
            }
        } else {
            int result = likedMapper.deleteById(liked.getId());
            if (result == 1) {
                return ResponseEntity.ok("{\"message\":\"取消点赞成功\"}");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"取消点赞失败\"}");
            }
        }
    }
}
