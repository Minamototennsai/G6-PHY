package g06.ecnu.heartbridge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import g06.ecnu.heartbridge.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * Mapper
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    @Delete("""
            DELETE FROM preferences_tag WHERE user_id = #{userId}
            """)
    void deletePreferenceTagByUserId(@Param("userId") int userId);

    @Insert("""
            INSERT INTO preferences_tag (user_id, tag_id)
            VALUES (#{userId}, #{tagId})
            """)
    void insertPreferenceTag(@Param("userId") int userId, @Param("tagId") int tagId);

    @Delete("""
            DELETE FROM expertise_tag WHERE user_id = #{userId}
            """)
    void deleteExpertiseTagByUserId(@Param("userId") int userId);

    @Insert("""
            INSERT INTO expertise_tag (user_id, tag_id)
            VALUES (#{userId}, #{tagId})
            """)
    void insertExpertiseTag(@Param("userId") int userId, @Param("tagId") int tagId);

    @Select("""
    SELECT t.name
    FROM tag t
    JOIN preferences_tag pt ON t.id = pt.tag_id
    WHERE pt.user_id = #{id}
    """)
    List<String> getClientTags (@Param("id") int id);

    @Select("""
    SELECT t.name
    FROM tag t
    JOIN expertise_tag et ON t.id = et.tag_id
    WHERE et.user_id = #{id}
    """)
    List<String> getConsultantTags (@Param("id") int id);

    @Select("""
    SELECT t.name
    FROM tag t
    JOIN forum_tag ft ON t.id = ft.tag_id
    WHERE ft.forum_id = #{id}
    """)
    List<String> getForumTags (@Param("id") int id);
}
