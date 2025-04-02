package g06.ecnu.heartbridge.controller;

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

    @GetMapping("/consultants/{consultant_id}/schedules")
    public ResponseEntity<Object> getSchedules(@PathVariable("consultant_id") int consultant_id) {
        return consultantService.getSchedules(consultant_id);
    }

    @PostMapping("/schedules/{schedule_id}")
    public ResponseEntity<Object> handleSchedule(@PathVariable("schedule_id") int schedule_id, @RequestParam int agree) {
        return consultantService.handleSchedule(schedule_id, agree);
    }

    @GetMapping("/consultants/list")
    public ResponseEntity<Object> getConsultant(@RequestParam(required = false) String keyword, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return consultantService.getConsultant(keyword, page, pageSize);
    }

    @GetMapping("/consultants/{consultant_id}")
    public ResponseEntity<Object> getConsultantById(@PathVariable("consultant_id") int consultant_id) {
        return consultantService.getConsultantById(consultant_id);
    }

    @GetMapping("/consultants/{consultant_id}/similar")
    public ResponseEntity<Object> getSimilarConsultant(@PathVariable("consultant_id") int consultant_id) {
        return consultantService.getSimilarConsultant(consultant_id);
    }

    @GetMapping("/consultants/recommended")
    public ResponseEntity<Object> getRecommendedConsultant(@RequestParam(required = false, defaultValue = "3") int count, @RequestAttribute("username") String username) {
        return consultantService.getRecommendedConsultants(username, count);
    }

    @GetMapping("/consultants/{consultant_id}/availability")
    public ResponseEntity<Object> getAvailability(@PathVariable("consultant_id") int consultant_id) {
        return consultantService.getAvailability(consultant_id);
    }
}
