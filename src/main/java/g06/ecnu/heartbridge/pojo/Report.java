package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/13 下午11:08
 **/
@Setter
@Getter
public class Report {
    private int id;
    private String type;
    private int sender_id;
    private int target_id;
}
