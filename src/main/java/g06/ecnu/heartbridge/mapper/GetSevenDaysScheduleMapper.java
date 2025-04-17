package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/17 上午9:16
 **/
@Mapper
public interface GetSevenDaysScheduleMapper {
    @Select("""
        select schedule.client_id,schedule.consultant_id,DATEDIFF(date,now()) AS sub,u1.username as un1,u2.username as un2
        from (schedule join (select id,users.username from users)as u1 on u1.id=schedule.client_id)join (select id,users.username from users)as u2 on u2.id=schedule.consultant_id
        where agree='accepted' and date>=now()-interval 7 day ;
    """)
    @Results({
            @Result(column = "client_id",property = "fromid"),
            @Result(column = "consultant_id",property = "toid"),
            @Result(column = "un1",property = "fromname"),
            @Result(column = "un2",property = "toname"),
            @Result(column = "sub",property = "date")
    })
    public List<ScheduleDTO> getSevenDaysSchedule();
}
