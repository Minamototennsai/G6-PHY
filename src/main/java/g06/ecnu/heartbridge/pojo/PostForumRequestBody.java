package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Getter
@Setter
public class PostForumRequestBody {
    private String title;
    private String content;
    private List<String> tags;
}
