package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 * PatchUserInfoRequestBody
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/9
 */
@Getter
@Setter
public class PatchUserInfoRequestBody {
    private String username;
    private String phone;
    private String email;
    private String profile;
    private String theme_preference;
    private List<Integer> preference;
    private List<Integer> expertise;
}
