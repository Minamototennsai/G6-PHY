package g06.ecnu.heartbridge.DTO;

import lombok.Data;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/17 上午9:19
 **/
@Data
public class ScheduleDTO {
    private int fromid;
    private String fromname;
    private int toid;
    private String toname;
    private int date;
}
