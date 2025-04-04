package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.MessageDTO;
import g06.ecnu.heartbridge.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    List<MessageDTO> getMessagesBySessionId(@Param("sessionId") int sessionId);
}
