package g06.ecnu.heartbridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import g06.ecnu.heartbridge.DTO.ConsultantDetailDTO;
import g06.ecnu.heartbridge.DTO.ConsultantTagDTO;
import g06.ecnu.heartbridge.entity.Users;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/24
 */
@Mapper
public interface ConsultantMapper extends BaseMapper<Users> {

    @Select("""
    <script>
        SELECT u.id, u.username, u.profile, GROUP_CONCAT(t.name) AS tags
        FROM users u
        JOIN expertise_tag et ON u.id = et.user_id
        JOIN tag t ON et.tag_id = t.id
        WHERE 1=1 AND u.status = 'active' AND u.type = 'consultant'
        <if test="keyword != null and keyword != ''">
            AND
                (u.username LIKE CONCAT('%', #{keyword}, '%')
                OR t.name LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        GROUP BY u.id
        LIMIT #{page}, #{pageSize}
    </script>
    """)
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "profile", property = "profile"),
            @Result(column = "tags", property = "tags", typeHandler = g06.ecnu.heartbridge.utils.StringToListTypeHandler.class)
    })
    List<ConsultantTagDTO> searchConsultants(@Param("keyword") String keyword,
                                          @Param("page") int page,
                                          @Param("pageSize") int pageSize);

    @Select("""
    <script>
        SELECT COUNT(DISTINCT u.id)
        FROM users u
        JOIN expertise_tag et ON u.id = et.user_id
        JOIN tag t ON et.tag_id = t.id
        WHERE 1=1 AND u.status = 'active' AND u.type = 'consultant'
        <if test="keyword != null and keyword != ''">
            AND
                (u.username LIKE CONCAT('%', #{keyword}, '%')
                OR t.name LIKE CONCAT('%', #{keyword}, '%'))
        </if>
    </script>
    """)
    int searchConsultantsCount(@Param("keyword") String keyword);

    @Select("""
    <script>
        SELECT u.id, u.username, u.profile, GROUP_CONCAT(t.name) AS tags
        FROM users u
        JOIN expertise_tag et ON u.id = et.user_id
        JOIN tag t ON et.tag_id = t.id
        WHERE
            u.id != #{consultantId}
            AND t.id = (SELECT tag_id FROM expertise_tag WHERE user_id = #{consultantId})
        GROUP BY u.id
    </script>
    """)
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "profile", property = "profile"),
            @Result(column = "tags", property = "tags", typeHandler = g06.ecnu.heartbridge.utils.StringToListTypeHandler.class)
    })
    List<ConsultantTagDTO> getSimilarConsultants(@Param("consultantId") int consultantId);

    @Select("""
    <script>
        SELECT u.id, u.username, GROUP_CONCAT(t.name) AS tags, u.profile, c.avg_score
        FROM users u
        JOIN expertise_tag et ON u.id = et.user_id
        JOIN tag t ON et.tag_id = t.id
        JOIN consultant_detail c ON u.id = c.user_id
        WHERE
             u.id = #{id}
        LIMIT 1
    </script>
    """)
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "tags", property = "tags", typeHandler = g06.ecnu.heartbridge.utils.StringToListTypeHandler.class),
            @Result(column = "profile", property = "profile"),
            @Result(column = "avg_score", property = "avgScore")
    })
    ConsultantDetailDTO getConsultantById(@Param("id") int consultantId);
}
