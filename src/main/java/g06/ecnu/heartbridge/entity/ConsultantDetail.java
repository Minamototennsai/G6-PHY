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
 * @since 2025-04-02
 */
@Getter
@Setter
public class ConsultantDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @JsonProperty("avg_score")
    private Double avgScore;

    @JsonProperty("evaluation_count")
    private Integer evaluationCount;

    private String certification;

    @JsonProperty("is_free")
    private String isFree;

    @JsonProperty("is_certificated")
    private String isCertificated;
}
