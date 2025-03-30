package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
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
public class Help implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private Integer sessionId;

    private Integer senderId;

    private String content;

    public Help(Integer sessionId, Integer senderId, String content) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.content = content;
    }
}
