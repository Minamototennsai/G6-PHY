package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-04-10
 */
@Getter
@Setter
@TableName("forum_tag")
public class ForumTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "forum_id", type = IdType.AUTO)
    private Integer forumId;

    private Integer tagId;
}
