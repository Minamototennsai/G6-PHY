package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.Report;
import lombok.Data;

import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/13 下午11:07
 **/
@Data
public class ReportsDTO {
    private int total;
    private List<Report>reports;
}
