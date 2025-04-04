package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ConsultantDetailDTO;
import g06.ecnu.heartbridge.pojo.ConsultantDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConsultantSearchMapperTest {
    @Autowired
    ConsultantSearchMapper consultantSearchMapper;
    @Test
    void getAllConsultants() {
        List<ConsultantDetail>detailDTOS=consultantSearchMapper.getAllConsultants();
        System.out.println(1);
    }
}