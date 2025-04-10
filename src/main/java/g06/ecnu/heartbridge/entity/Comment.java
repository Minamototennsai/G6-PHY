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
 * @since 2025-04-08
 */
@Getter
@Setter
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    private String content;

    @JsonProperty("comment_type")
    private String commentType;

    @JsonProperty("target_id")
    private Integer targetId;

    @JsonProperty("create_time")
    private LocalDateTime createTime;

    @JsonProperty("liked_count")
    private Integer likedCount;
}
