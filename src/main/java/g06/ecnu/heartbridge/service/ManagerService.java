package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.*;
import g06.ecnu.heartbridge.mapper.ManagerMapper;
import g06.ecnu.heartbridge.pojo.ConsultantCertificatedInfo;
import g06.ecnu.heartbridge.pojo.IdAndContent;
import g06.ecnu.heartbridge.pojo.Report;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/10 上午9:40
 **/
@Service
public class ManagerService {
    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private ChatService chatService;

    public ResponseEntity<ConsultantApplyDTO> getConsultantApplications(int page){
        if(page==0)page=1;
        List<ConsultantCertificatedInfo> list = managerMapper.getAllHaveNotBeenCertificatedConsultant();
        List<ConsultantCertificatedInfo> result = new ArrayList<>();
        for(int i=(page-1)*10;i<page*10&&i<list.size();i++){
            result.add(list.get(i));
        }
        ConsultantApplyDTO dto = new ConsultantApplyDTO();
        dto.setApplications(result);
        dto.setTotal(result.size());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<CheckResultDTO> checkApply(IdActionDTO idActionDTO) {
        if(idActionDTO.getAction().equals("approve")){
            managerMapper.updateConsultantById(idActionDTO.getId(),"yes");
            CheckResultDTO dto = new CheckResultDTO();
            dto.setId(idActionDTO.getId());
            dto.set_certificated(true);
            return ResponseEntity.ok(dto);
        }else{
            managerMapper.deleteConsultantById(idActionDTO.getId());
            managerMapper.deleteUserById(idActionDTO.getId());
            CheckResultDTO dto = new CheckResultDTO();
            dto.setId(idActionDTO.getId());
            dto.set_certificated(false);
            return ResponseEntity.ok(dto);
        }
    }


    public ResponseEntity<ReportsDTO> getReportsList(int page){
        if(page==0)page=1;
        List<Report> list=managerMapper.getAllReport();
        list=list.subList((page-1)*10,page*10);
        ReportsDTO dto = new ReportsDTO();
        dto.setReports(list);
        dto.setTotal(list.size());
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<MessageDTO> deleteReport(int id){
        managerMapper.deleteReportById(id);
        MessageDTO dto = new MessageDTO();
        dto.setMessage("操作成功");
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<MessageDTO> closeSession(int id){
        chatService.closeSession(id);
        MessageDTO dto = new MessageDTO();
        dto.setMessage("操作成功");
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<MessageDTO> grantUsers(int id,int status){
        if(status==1){
            managerMapper.banUser(id);
        }else{
            managerMapper.unbanUser(id);
        }
        MessageDTO dto=new MessageDTO();
        dto.setMessage("操作成功");
        return ResponseEntity.ok(dto);
    }


    public ResponseEntity<ReportByIdUserDTO> searchUser(int id){
        return ResponseEntity.ok(managerMapper.searchUserById(id));
    }

    public ResponseEntity<ReportByIdContentDTO> searchEvaluate(int id){
        return ResponseEntity.ok(managerMapper.searchEvaluateById(id));
    }

    public ResponseEntity<ReportByIdContentDTO> searchArticle(int id){
        return ResponseEntity.ok(managerMapper.searchArticleById(id));
    }

    public ResponseEntity<ReportByIdContentDTO> searchForum(int id){
        return ResponseEntity.ok(managerMapper.searchForumById(id));
    }

    public ResponseEntity<ReportSessionLogsDTO> searchSessionLogs(int id){
        List<IdAndContent> list=managerMapper.searchAllSessionLogsById(id);
        ReportSessionLogsDTO dto = new ReportSessionLogsDTO();
        dto.setLogs(list);
        return ResponseEntity.ok(dto);
    }

}
