package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-21
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Help implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("session_id")
    private Integer sessionId;

    @JsonProperty("sender_id")
    private Integer senderId;

    private String content;

    public Help(Integer sessionId, Integer senderId, String content) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.content = content;
    }
}
