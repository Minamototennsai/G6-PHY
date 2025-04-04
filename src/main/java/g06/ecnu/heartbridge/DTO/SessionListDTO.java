package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * Session List
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/3
 */
@Data
public class SessionListDTO {
    private String sessionId;
    private List<String> username;
    private String endTime;
}
