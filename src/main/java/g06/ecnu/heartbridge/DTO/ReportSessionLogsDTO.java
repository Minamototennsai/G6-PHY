package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.IdAndContent;
import lombok.Data;

import java.util.List;


/**
 * @author 璃樘鼎臻
 * @since 2025/4/14 下午5:16
 **/
@Data
public class ReportSessionLogsDTO {
    private List<IdAndContent> logs;
}