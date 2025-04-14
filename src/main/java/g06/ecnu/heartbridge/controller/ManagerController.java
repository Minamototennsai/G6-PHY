package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.DTO.*;
import g06.ecnu.heartbridge.pojo.Report;
import g06.ecnu.heartbridge.service.ManagerService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author 璃樘鼎臻
 * @since 2025/4/10 上午9:19
 **/
@Controller
@RequestMapping("/api/admin")
public class ManagerController {
    @Autowired
    ManagerService managerService;

    @GetMapping("/consultant-applications")
    public ResponseEntity<ConsultantApplyDTO> getConsultantApplications(int page) {
        return managerService.getConsultantApplications(page);
    }

    @PostMapping("/consultant-applications")
    public ResponseEntity<CheckResultDTO> checkApply(@RequestBody IdActionDTO idActionDTO) {
        return managerService.checkApply(idActionDTO);
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportsDTO> getReportsList(int page){
        return managerService.getReportsList(page);
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<MessageDTO> deleteReport(@PathVariable int id) {
        return managerService.deleteReport(id);
    }

    @PostMapping("/sessions/{id}")
    public ResponseEntity<MessageDTO> closeSession(@PathVariable int id) {
        return managerService.closeSession(id);
    }

    @PatchMapping("/users")
    public ResponseEntity<MessageDTO> grantUsers(@RequestParam int user_id,@RequestParam int status){
        return managerService.grantUsers(user_id, status);
    }

    @GetMapping("/search/user")
    public ResponseEntity<ReportByIdUserDTO> searchUser(@RequestParam int id){
        return managerService.searchUser(id);
    }

    @GetMapping("/search/evaluate")
    public ResponseEntity<ReportByIdContentDTO> searchEvaluate(@RequestParam int id){
        return managerService.searchEvaluate(id);
    }

    @GetMapping("/search/article")
    public ResponseEntity<ReportByIdContentDTO>searchArticle(@RequestParam int id){
        return managerService.searchArticle(id);
    }

    @GetMapping("/search/forum")
    public ResponseEntity<ReportByIdContentDTO>searchForum(@RequestParam int id){
        return managerService.searchForum(id);
    }

    @GetMapping("/search/session")
    public ResponseEntity<ReportSessionLogsDTO>searchSession(@RequestParam int id){
        return managerService.searchSessionLogs(id);
    }
}
