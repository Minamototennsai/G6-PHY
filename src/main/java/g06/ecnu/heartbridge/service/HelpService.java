package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.DTO.HelpDTO;
import g06.ecnu.heartbridge.entity.Help;
import g06.ecnu.heartbridge.entity.Sessions;
import g06.ecnu.heartbridge.mapper.HelpMapper;
import g06.ecnu.heartbridge.mapper.SessionsMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private SessionsMapper sessionMapper;

    //获取求助
    public ResponseEntity<Object> getHelp(Integer helpId){
        List<Help> helps;
        List<HelpDTO> helpDTOs = new ArrayList<>();
        if (helpId == null) {
            helps = helpMapper.selectList(null);
        } else {
            QueryWrapper<Help> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", helpId);
            helps = helpMapper.selectList(queryWrapper);
        }
        for (Help help : helps) {
            HelpDTO helpDTO = new HelpDTO(help);
            helpDTO.setUsername(usersMapper.selectById(help.getSenderId()).getUsername());
            Sessions session = sessionMapper.selectById(help.getSessionId());
            if (session.getEndTime() != null) {
                helpDTOs.add(helpDTO);
            }
        }
        Map<String, List<HelpDTO>> response = new HashMap<>();
        response.put("data", helpDTOs);
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

    @Transactional
    public ResponseEntity<Object> handleHelp(int helpId, int consultantId) {
        QueryWrapper<Help> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", helpId);
        Help help = helpMapper.selectOne(queryWrapper);
        int sessionId = help.getSessionId();
        int success = chatService.joinSession(consultantId, sessionId);
        helpMapper.deleteById(helpId);
        if (success == 0) {
            return ResponseEntity.ok("{\"message\":\"成功加入咨询\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"加入咨询失败\"}");
        }
    }
}
