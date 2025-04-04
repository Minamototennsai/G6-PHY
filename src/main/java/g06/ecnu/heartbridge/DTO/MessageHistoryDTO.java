package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * message
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/3
 */
@Data
public class MessageHistoryDTO {
    private int msg_id;
    private int sender_id;
    private String sender_name;
    private LocalDateTime send_time;
    private String content;
}
