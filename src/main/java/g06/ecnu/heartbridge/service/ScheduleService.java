package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Schedule;
import g06.ecnu.heartbridge.entity.Users;
import g06.ecnu.heartbridge.mapper.ScheduleMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/19
 */
@Service
public class ScheduleService {
    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private UsersMapper usersMapper;

    public ResponseEntity<Object> getSchedule(int clientId) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("client_id", clientId);
        List<Schedule> schedules = scheduleMapper.selectList(queryWrapper);
        if (!schedules.isEmpty()) {
            Map<String, List<Schedule>> response = new HashMap<>();
            response.put("data", schedules);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到预约信息\"}");
        }

    }

    public ResponseEntity<Object> saveSchedule(int consultantId, int clientId, String date, String time) {
        LocalDate localDate;
        LocalTime localTime;
        try{
            localDate = LocalDate.parse(date);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败：日期不合法\"}");
        }
        try{
            localTime = LocalTime.parse(time);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败：时间不合法\"}");
        }
        int timeConverted =localTime.getHour()*2 + localTime.getMinute()/30;
        Schedule schedule = new Schedule(consultantId, clientId, localDate, timeConverted);
        int result = scheduleMapper.insert(schedule);
        if (result > 0) {
            return ResponseEntity.ok("{\"message\":\"预约成功\"}");
        } else {
            QueryWrapper<Users> consultantWrapper = new QueryWrapper<>();
            consultantWrapper.eq("id", consultantId);
            boolean consultantExists = usersMapper.selectCount(consultantWrapper) > 0;
            QueryWrapper<Users> clientWrapper = new QueryWrapper<>();
            clientWrapper.eq("id", clientId);
            boolean clientExists = usersMapper.selectCount(clientWrapper) > 0;
            if (!consultantExists && clientExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败：未找到咨询师\"}");
            } else if (consultantExists && !clientExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败：未找到用户\"}");
            } else if (!consultantExists && !clientExists) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败：未找到咨询师和用户\"}");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"预约失败\"}");
        }
    }
}
