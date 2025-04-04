package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.DTO.SuggestConsultantListDTO;
import g06.ecnu.heartbridge.service.ConsultantSuggestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 响应推荐咨询师
 *
 * @author 璃樘鼎臻
 * @since 2025/4/3 下午10:45
 **/
@Controller
@RequestMapping("/api/consultants")
public class ConsultantSuggestController {
    @Autowired
    private ConsultantSuggestService service;
    @GetMapping("/recommended")
    public ResponseEntity<SuggestConsultantListDTO> getSuggestConsultants(@RequestParam(required = false, defaultValue = "3")Integer count, HttpServletRequest request) {
        return service.getSuggestConsultants(count,request);
    }
}
