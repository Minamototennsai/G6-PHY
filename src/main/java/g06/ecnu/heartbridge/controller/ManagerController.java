package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.DTO.CheckResultDTO;
import g06.ecnu.heartbridge.DTO.ConsultantApplyDTO;
import g06.ecnu.heartbridge.DTO.IdActionDTO;
import g06.ecnu.heartbridge.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    
}
