package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import g06.ecnu.heartbridge.entity.Schedule;
import g06.ecnu.heartbridge.mapper.ScheduleMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class ConsultantService {
    @Resource
    private ScheduleMapper scheduleMapper;

    public ResponseEntity<Object> getAvailableTimes(int consultantId, String date) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultant_id", consultantId)
                .eq("date", date)
                .eq("agree", 1);
        List<Schedule> schedules = scheduleMapper.selectList(queryWrapper);
        List<String> availableTimesConverted = convertAvailableTimes(schedules);
        Map<String, List<String>> response = new HashMap<>();
        response.put("data", availableTimesConverted);
        if (!availableTimesConverted.isEmpty()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"该咨询师当日已约满\"}");
        }
    }

    public ResponseEntity<Object> handleSchedule(int scheduleId, int agree) {
        UpdateWrapper<Schedule> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("schedule_id", scheduleId).set("agree", agree);
        if (scheduleMapper.update(updateWrapper) > 0){
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"修改成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"修改失败\"}");
        }
    }

    public ResponseEntity<Object> getSchedules(int consultantId) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultant_id", consultantId)
                .eq("agree", 0);
        List<Schedule> schedules = scheduleMapper.selectList(queryWrapper);
        if (!schedules.isEmpty()) {
            Map<String, List<Schedule>> response = new HashMap<>();
            response.put("data", schedules);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"当前暂无预约请求\"}");
        }
    }

    //将schedule的格式为0-47的可用时间转为HH:mm格式
    private static List<String> convertAvailableTimes(List<Schedule> schedules) {
        List<String> availableTimes = new ArrayList<>();
        for (int i = 0; i < 47; i++){
            availableTimes.add(String.valueOf(i));
        }
        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                availableTimes.remove(String.valueOf(schedule.getTime()));
            }
        }
        List<String> availableTimesConverted = new ArrayList<>();
        for (String time : availableTimes) {
            availableTimesConverted.add(String.format("%s:%s", Integer.getInteger(time)/2, (Integer.getInteger(time)%2)==0?"00":"30" ));
        }
        return availableTimesConverted;
    }
}
