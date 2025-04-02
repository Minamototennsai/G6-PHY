package g06.ecnu.heartbridge.DTO;

import lombok.Data;


/**
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午9:45
 **/
@Data
public class ArticleDTO {
    private Integer article_id;
    private String title;
    private String writer_name;
    private String preview;
    private Integer view_count;
    private Integer liked_count;
    private String create_time;
    private String tag;
}
