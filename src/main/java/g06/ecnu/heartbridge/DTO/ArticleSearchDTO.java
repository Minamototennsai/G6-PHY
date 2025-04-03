package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.ArticleResponseData;
import lombok.Data;

/**
 * 返回文章搜索的结果
 *
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午5:29
 **/
@Data
public class ArticleSearchDTO {
    private ArticleResponseData data;
}
