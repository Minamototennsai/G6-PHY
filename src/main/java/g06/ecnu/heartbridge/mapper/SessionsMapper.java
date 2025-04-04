package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.SessionListDTO;
import g06.ecnu.heartbridge.entity.Sessions;
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
public interface SessionsMapper extends BaseMapper<Sessions> {
    @Select("""
        SELECT s.id AS session_id,
               s.end_time,
               GROUP_CONCAT(u.username ORDER BY u.id SEPARATOR ', ') AS username
        FROM sessions s
        JOIN user_session us ON s.id = us.session_id
        JOIN users u ON u.id IN (us.client_id, us.consultant_id)
        WHERE
            (#{userType} = '0' AND us.client_id = #{userId})
            OR
            (#{userType} = '1' AND us.consultant_id = #{userId})
        GROUP BY s.id, s.end_time
    """)
    @Results({
            @Result(column = "session_id", property = "sessionId"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "username", property = "username",
                    javaType = List.class,
                    typeHandler = g06.ecnu.heartbridge.utils.StringToListTypeHandler.class)
    })
    List<SessionListDTO> getSessionsByUserId(@Param("userId") int userId, @Param("userType") String userType);

    @Select("""
        SELECT s.id AS session_id,
               s.end_time,
               GROUP_CONCAT(u.username ORDER BY u.id SEPARATOR ', ') AS username
        FROM sessions s
        JOIN user_session us ON s.id = us.session_id
        JOIN users u ON u.id IN (us.client_id, us.consultant_id)
        WHERE s.id = #{sessionId}
        GROUP BY s.id, s.end_time
    """)
    @Results({
            @Result(column = "session_id", property = "sessionId"),
            @Result(column = "end_time", property = "endTime"),
            @Result(column = "username", property = "username",
                    javaType = List.class,
                    typeHandler = g06.ecnu.heartbridge.utils.StringToListTypeHandler.class)
    })
    SessionListDTO getSessionById(@Param("sessionId") int sessionId);
}
