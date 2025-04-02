package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 文章
 *
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午5:45
 **/
@Setter
@Getter
public class Article {
    private Integer article_id;
    private String title;
    private String writer_name;
    private String preview;
    private Integer views_count;
    private Integer liked_count;
    private String create_time;
    private List<String> tags;
}
