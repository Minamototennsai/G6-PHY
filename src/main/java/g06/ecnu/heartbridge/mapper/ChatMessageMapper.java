package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.MessageHistoryDTO;
import g06.ecnu.heartbridge.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-30
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    @Select("""
        SELECT c.id AS msg_id, sender_id, u.username AS sender_name, send_time AS send_time, content
        FROM chat_message c
        JOIN users u ON u.id = c.sender_id
        WHERE c.session_id = #{sessionId}
    """)
    @Results({
            @Result(column = "msg_id", property = "msg_id"),
            @Result(column = "sender_id", property = "sender_id"),
            @Result(column = "sender_name", property = "sender_name"),
            @Result(column = "send_time", property = "send_time"),
            @Result(column = "content", property = "content")
    })
    List<MessageHistoryDTO> getMessagesBySessionId(@Param("sessionId") int sessionId);
}
