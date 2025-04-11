package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.CheckResultDTO;
import g06.ecnu.heartbridge.DTO.ConsultantApplyDTO;
import g06.ecnu.heartbridge.DTO.IdActionDTO;
import g06.ecnu.heartbridge.mapper.ManagerMapper;
import g06.ecnu.heartbridge.pojo.ConsultantCertificatedInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
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
}
