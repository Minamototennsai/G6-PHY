package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ScheduleDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GetSevenDaysScheduleTest {
    @Autowired
    GetSevenDaysScheduleMapper mapper;
    @Test
    void getSevenDaysSchedule() {
        List<ScheduleDTO> schedules = mapper.getSevenDaysSchedule();
        for(int i=0;i<schedules.size();i++){
            System.out.println(schedules.get(i).getToid());
            System.out.println(schedules.get(i).getToname());
            System.out.println(schedules.get(i).getFromid());
            System.out.println(schedules.get(i).getFromname());
            System.out.println(schedules.get(i).getDate());
        }
    }
}