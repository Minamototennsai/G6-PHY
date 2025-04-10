package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.entity.Tag;
import g06.ecnu.heartbridge.mapper.TagMapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Service
public class TagService {
    @Resource
    private TagMapper tagMapper;

    public ResponseEntity<Object> getTags(){
        Map<String, Object> response = new HashMap<>();
        List<Tag> tags = tagMapper.selectList(null);
        response.put("data", tags);
        return ResponseEntity.ok(response);
    }
}
