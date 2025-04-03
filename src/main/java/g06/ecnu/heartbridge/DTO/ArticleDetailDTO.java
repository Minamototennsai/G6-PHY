package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

/**
 * 进入文章的返回体
 *
 * @author 璃樘鼎臻
 * @since 2025/4/2 下午5:44
 **/
@Data
public class ArticleDetailDTO {
    private String title;
    private String writer_name;
    private String content;
    private Integer views_count;
    private Integer liked_count;
    private String create_time;
    private List<String> tags;
}
