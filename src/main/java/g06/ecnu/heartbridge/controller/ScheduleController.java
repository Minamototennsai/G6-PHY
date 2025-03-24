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
    public ResponseEntity<Object> getSchedule(@RequestParam int clientId){
        return scheduleService.getSchedule(clientId);
    }

    @PostMapping("/schedules")
    public ResponseEntity<Object> saveSchedule(@RequestParam int consultantId, @RequestParam int clientId, @RequestParam String date, @RequestParam String time) {
        return scheduleService.saveSchedule(consultantId, clientId, date, time);
    }
}
