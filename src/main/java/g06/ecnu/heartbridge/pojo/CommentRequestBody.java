package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * CommentRequestBody
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/9
 */
@Getter
@Setter
public class CommentRequestBody {
    private int type;
    private int target_id;
    private String content;
}
