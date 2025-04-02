package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/2 上午12:40
 **/
@Data
public class UserWithPreferAndArticleHistoryDTO {
    private Integer userId;
    private List<String> preferTags;
    private List<String> historyTags;
}
