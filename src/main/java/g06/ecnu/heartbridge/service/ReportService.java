package g06.ecnu.heartbridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.Report;
import g06.ecnu.heartbridge.mapper.ReportMapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Service
public class ReportService {
    @Resource
    private ReportMapper reportMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Object> report(int sender_id, String type, int target_id) {
        Report report = new Report();
        report.setSenderId(sender_id);
        report.setType(type);
        report.setTargetId(target_id);
        int result = reportMapper.insert(report);
        if (result == 1) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            data.put("id", report.getId());
            data.put("time", LocalDateTime.now().toString());
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("{\"message\":\"举报失败，请稍后再试\"}");
        }
    }
}
