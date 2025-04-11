package g06.ecnu.heartbridge.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * RequestBody
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Getter
@Setter
public class ChangePasswordRequestBody {
    private String current_password;
    private String new_password;
}
