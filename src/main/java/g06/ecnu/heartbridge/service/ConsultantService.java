package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.DTO.ConsultantDetailDTO;
import g06.ecnu.heartbridge.DTO.ConsultantTagDTO;
import g06.ecnu.heartbridge.entity.ConsultantDetail;
import g06.ecnu.heartbridge.entity.Schedule;
import g06.ecnu.heartbridge.entity.Users;
import g06.ecnu.heartbridge.mapper.ConsultantDetailMapper;
import g06.ecnu.heartbridge.mapper.ConsultantMapper;
import g06.ecnu.heartbridge.mapper.ScheduleMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Resource
    private ConsultantMapper consultantMapper;

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private ChatService chatService;

    @Resource
    private ConsultantDetailMapper consultantDetailMapper;

    /*
        根据咨询师id来查询可预约时间：
        先查询指定日期的预约，如果没有预约则全天可用，查询到预约就在可用时间中删除已预约的时间
     */
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

    //处理预约
    public ResponseEntity<Object> handleSchedule(int scheduleId, int agree) {
        UpdateWrapper<Schedule> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("schedule_id", scheduleId).set("agree", agree);
        if (scheduleMapper.update(updateWrapper) > 0){
            return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"修改成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"修改失败\"}");
        }
    }

    //查询预约
    public ResponseEntity<Object> getSchedules(int consultantId) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultant_id", consultantId);
        List<Schedule> schedules = scheduleMapper.selectList(queryWrapper);
        if (!schedules.isEmpty()) {
            Map<String, List<Schedule>> response = new HashMap<>();
            response.put("data", schedules);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"当前暂无预约请求\"}");
        }
    }

    //根据关键词搜索咨询师
    public ResponseEntity<Object> getConsultant(String keyword, Integer page, Integer pageSize) {
        List<ConsultantTagDTO> result = consultantMapper.searchConsultants(keyword, page, pageSize);
        if (!result.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            data.put("total", result.size());
            data.put("page", page+1);
            data.put("pageSize", pageSize);
            data.put("list", result);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到咨询师\"}");
        }
    }

    //根据相同的tag查询相似咨询师
    public ResponseEntity<Object> getSimilarConsultant(int consultantId) {
        List<ConsultantTagDTO> result = consultantMapper.getSimilarConsultants(consultantId);
        if (!result.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到咨询师\"}");
        }
    }

    //根据id搜索指定咨询师
    public ResponseEntity<Object> getConsultantById(int consultantId) {
        ConsultantDetailDTO result = consultantMapper.getConsultantById(consultantId);
        if (result != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到咨询师\"}");
        }
    }

    public ResponseEntity<Object> getAvailability(int consultantId) {
        QueryWrapper<ConsultantDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", consultantId);
        ConsultantDetail consultantDetail = consultantDetailMapper.selectOne(queryWrapper);
        if (consultantDetail != null) {
            String isFree = consultantDetail.getIsFree();
            ObjectNode response = new ObjectMapper().createObjectNode();
            ObjectNode data = new ObjectMapper().createObjectNode();
            data.put("isAvailable", isFree.equals("yes"));
            data.put("isOnline", chatService.ifUserOnline(consultantId));
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到咨询师\"}");
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

    //定时任务，每半小时更新一次咨询师是否有空（当前无预约且咨询师不在咨询中）
    @Async
    @Scheduled(cron = "0 0,30 * * * *")
    @Transactional
    protected void updateConsultantAvailability(){
        LocalTime now = LocalTime.now();
        int currentHalfHour = now.getHour()*2 + now.getMinute()/30;
        QueryWrapper<Users> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("type", "consultant");
        List<Users> consultants = usersMapper.selectList(userQueryWrapper);
        for (Users user : consultants) {
            UpdateWrapper<ConsultantDetail> updateWrapper = new UpdateWrapper<>();
            if (ifConsultantAvailable(user.getId(), currentHalfHour)) {
                updateWrapper.eq("user_id", user.getId())
                        .set("is_free", "yes");
                consultantDetailMapper.update(updateWrapper);
            } else {
                updateWrapper.eq("user_id", user.getId())
                        .set("is_free", "no");
                consultantDetailMapper.update(updateWrapper);
            }
        }
    }

    private boolean ifConsultantAvailable(int consultantId, int halfHour) {
        QueryWrapper<Schedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultant_id", consultantId)
                .eq("time", halfHour)
                .eq("date", LocalDate.now());
        return !(scheduleMapper.selectCount(queryWrapper) > 0 //当前存在预约
                &&
                chatService.ifUserInSession(consultantId)); //咨询师正在咨询
    }
}
