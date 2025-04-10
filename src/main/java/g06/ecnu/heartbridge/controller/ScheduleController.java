package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/19
 */
@Controller
@RequestMapping("/api")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {this.scheduleService = scheduleService;}

    @GetMapping("/schedules")
    public ResponseEntity<Object> getSchedule(@RequestParam int client_id){
        return scheduleService.getSchedule(client_id);
    }

    @PostMapping("/schedules")
    public ResponseEntity<Object> addSchedule(@RequestParam int consultant_id, @RequestParam int client_id, @RequestParam String date, @RequestParam String time) {
        return scheduleService.addSchedule(consultant_id, client_id, date, time);
    }
}
