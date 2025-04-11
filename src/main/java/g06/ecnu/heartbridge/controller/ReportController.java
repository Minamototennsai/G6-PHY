package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Controller
@RequestMapping("/api")
public class ReportController {
    private final ReportService reportService;
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/reports")
    public ResponseEntity<Object> report(@RequestAttribute("userId") int sender_id, @RequestParam String type, @RequestParam int target_id ) {
        return reportService.report(sender_id, type, target_id);
    }
}
