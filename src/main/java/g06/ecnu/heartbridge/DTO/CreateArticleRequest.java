package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CreateArticleRequest {
    private String title;
    private String content;
    private List<String> tags;
}