package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.ConsultantCertificatedInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/10 上午9:33
 **/
@Data
public class ConsultantApplyDTO{
    private int total;
    private List<ConsultantCertificatedInfo> applications;
}
