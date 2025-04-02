package g06.ecnu.heartbridge.pojo;

import g06.ecnu.heartbridge.service.ArticleService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午5:43
 **/
@Getter
@Setter
public class ArticleResponseData {
    private Integer total;
    private List<Article> articles;
}
