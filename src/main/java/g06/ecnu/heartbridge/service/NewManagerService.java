package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.ConsultantDetail;
import g06.ecnu.heartbridge.entity.Schedule;
import g06.ecnu.heartbridge.mapper.ConsultantDetailMapper;
import g06.ecnu.heartbridge.mapper.ScheduleMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/16
 */
@Service
public class NewManagerService {
    @Resource
    private ChatService chatService;

    @Resource
    private ConsultantDetailMapper consultantDetailMapper;

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private ScheduleMapper scheduleMapper;

    public ResponseEntity<Object> getDaily() {
        return ResponseEntity.ok("{\"num\": " + chatService.getDaily() + "}");
    }

    public ResponseEntity<Object> getScorerank(){
        QueryWrapper<ConsultantDetail> consultantDetailQueryWrapper = new QueryWrapper<>();
        consultantDetailQueryWrapper.orderByDesc("avg_score")
                .last("LIMIT 10");
        List<ConsultantDetail> consultantDetails = consultantDetailMapper.selectList(consultantDetailQueryWrapper);
        ObjectNode response = new ObjectMapper().createObjectNode();
        ObjectNode consultants = new ObjectMapper().createObjectNode();
        ArrayNode consultantsArray = new ObjectMapper().createArrayNode();
        for (ConsultantDetail consultantDetail : consultantDetails) {
            ObjectNode consultantsNode = new ObjectMapper().createObjectNode();
            consultantsNode.put("id", consultantDetail.getUserId());
            consultantsNode.put("name", usersMapper.selectById(consultantDetail.getUserId()).getUsername());
            consultantsNode.put("score", consultantDetail.getAvgScore());
            consultantsArray.add(consultantsNode);
        }
        consultants.set("consultants", consultantsArray);
        response.set("consultants", consultants);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Object> getSessionRank(){
        QueryWrapper<Schedule> scheduleQueryWrapper = new QueryWrapper<>();
        scheduleQueryWrapper.select("consultant_id, COUNT(consultant_id) as count")
                .groupBy("consultant_id")
                .orderByDesc("count")
                .last("LIMIT 10");
        List<Map<String, Object>> topSessions = scheduleMapper.selectMaps(scheduleQueryWrapper);
        topSessions.sort((map1, map2) -> {
            Long count1 = (Long) map1.get("count");
            Long count2 = (Long) map2.get("count");
            return count2.compareTo(count1);
        });
        ObjectNode response = new ObjectMapper().createObjectNode();
        ArrayNode topSessionsArray = new ObjectMapper().createArrayNode();
        for (Map<String, Object> map : topSessions) {
            ObjectNode topSessionsNode = new ObjectMapper().createObjectNode();
            topSessionsNode.put("id", map.get("consultant_id").toString());
            topSessionsNode.put("name", usersMapper.selectById(Integer.parseInt(map.get("consultant_id").toString())).getUsername());
            topSessionsNode.put("num_session", map.get("count").toString());
            topSessionsArray.add(topSessionsNode);
        }
        response.set("consultants", topSessionsArray);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Object> getNowSession(){
        List<Integer> sessionList = chatService.getCurrentSessions();
        ObjectNode response = new ObjectMapper().createObjectNode();
        ArrayNode sessionsArray = new ObjectMapper().createArrayNode();
        for (Integer sessionId : sessionList) {
            sessionsArray.add(sessionId);
        }
        response.put("total", sessionList.size());
        response.set("sessions", sessionsArray);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Object> getScheduleWeek(){
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        QueryWrapper<Schedule> scheduleQueryWrapper = new QueryWrapper<>();
        scheduleQueryWrapper.eq("agree", "accepted")
                .ge("date", today)
                .lt("date", sevenDaysLater);
        List<Schedule> schedules = scheduleMapper.selectList(scheduleQueryWrapper);
        ObjectNode response = new ObjectMapper().createObjectNode();
        ArrayNode schedulesArray = new ObjectMapper().createArrayNode();
        for (Schedule schedule : schedules) {
            ObjectNode scheduleNode = new ObjectMapper().createObjectNode();
            scheduleNode.put("fromid", schedule.getClientId());
            scheduleNode.put("fromname", usersMapper.selectById(schedule.getClientId()).getUsername());
            scheduleNode.put("toid", schedule.getClientId());
            scheduleNode.put("toname", usersMapper.selectById(schedule.getConsultantId()).getUsername());
            scheduleNode.put("time", schedule.getTime());
            schedulesArray.add(scheduleNode);
        }
        response.set("schedules", schedulesArray);
        return ResponseEntity.ok(response);
    }
}
