package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Help;
import g06.ecnu.heartbridge.mapper.HelpMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
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
 * @since 2025/3/24
 */
@Service
public class HelpService {
    @Resource
    private HelpMapper helpMapper;

    @Resource
    private ChatService chatService;

    //获取求助
    public ResponseEntity<Object> getHelp(Integer helpId){
        List<Help> helps;
        if (helpId == null) {
            helps = helpMapper.selectList(null);
        } else {
            QueryWrapper<Help> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", helpId);
            helps = helpMapper.selectList(queryWrapper);
        }
        Map<String, List<Help>> response = new HashMap<>();
        response.put("data", helps);
        if (response.get("data") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到求助\"}");
        } else {
            return ResponseEntity.ok(response);
        }
    }

    //新增求助
    public ResponseEntity<Object> addHelp(String uuid, int consultantId, int sessionId, String content) {
        Help help = new Help();
        help.setUuid(uuid);
        help.setSenderId(consultantId);
        help.setSessionId(sessionId);
        help.setContent(content);
        int result = helpMapper.insert(help);
        if (result > 0) {
            return ResponseEntity.ok("{\"message\":\"创建求助成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"创建求助失败\"}");
        }
    }

    public ResponseEntity<Object> handleHelp(int helpId, int consultantId) {
        QueryWrapper<Help> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", helpId);
        Help help = helpMapper.selectOne(queryWrapper);
        int sessionId = help.getSessionId();
        int success = chatService.joinSession(consultantId, sessionId);
        if (success == 0) {
            return ResponseEntity.ok("{\"message\":\"成功加入咨询\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"加入咨询失败\"}");
        }
    }
}
