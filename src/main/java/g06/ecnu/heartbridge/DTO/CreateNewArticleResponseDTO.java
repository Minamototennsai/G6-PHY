package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.CreateNewArticleResponse;
import lombok.Data;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/3 下午2:48
 **/
@Data
public class CreateNewArticleResponseDTO {
    private String message;
    private CreateNewArticleResponse data;
}
