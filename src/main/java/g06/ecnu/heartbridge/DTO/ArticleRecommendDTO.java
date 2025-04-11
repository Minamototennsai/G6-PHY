package g06.ecnu.heartbridge.DTO;

import g06.ecnu.heartbridge.pojo.Article;
import lombok.Data;

import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/10 上午8:44
 **/
@Data
public class ArticleRecommendDTO {
    private int total;
    private List<Article>articles;
}
