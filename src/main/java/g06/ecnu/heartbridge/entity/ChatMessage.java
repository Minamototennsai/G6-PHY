package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-30
 */
@Getter
@Setter
public class ChatMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("session_id")
    private Integer sessionId;

    @JsonProperty("sender_id")
    private Integer senderId;

    private String content;

    @JsonProperty("send_time")
    private LocalDateTime sendTime;
}
