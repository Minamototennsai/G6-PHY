package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.entity.Schedule;
import g06.ecnu.heartbridge.service.ConsultantService;
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
public class ConsultantController {
    public final ConsultantService consultantService;

    @Autowired
    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

    @GetMapping("/consultants/{consultant_id}/available-times")
    public ResponseEntity<Object> getAvailableTimes(@PathVariable("consultant_id") int consultant_id, @RequestParam String date) {
        return consultantService.getAvailableTimes(consultant_id, date);
    }

    @GetMapping("/api/consultants/{consultant_id}/schedules")
    public ResponseEntity<Object> getSchedules(@PathVariable("consultant_id") int consultant_id) {
        return consultantService.getSchedules(consultant_id);
    }

    @PostMapping("/api/schedules/{schedule_id}")
    public ResponseEntity<Object> handleSchedule(@PathVariable("schedule_id") int schedule_id, @RequestParam int agree) {
        return consultantService.handleSchedule(schedule_id, agree);
    }
}
