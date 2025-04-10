package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;

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
public class UserSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("sender_id")
    private Integer sessionId;

    @JsonProperty("client_id")
    private Integer clientId;

    @JsonProperty("consultant_id")
    private Integer consultantId;

    @JsonProperty("is_evaluated")
    private Integer isEvaluated;
}
