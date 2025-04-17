package g06.ecnu.heartbridge.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import g06.ecnu.heartbridge.entity.Help;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * DTO
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/17
 */
@Getter
@Setter
public class HelpDTO {
    private Integer id;
    private String username;
    private String uuid;

    @JsonProperty("session_id")
    private Integer sessionId;

    @JsonProperty("sender_id")
    private Integer senderId;

    private String content;

    public HelpDTO(Help help) {
        this.id = help.getId();
        this.uuid = help.getUuid();
        this.sessionId = help.getSessionId();
        this.senderId = help.getSenderId();
        this.content = help.getContent();
    }
}
