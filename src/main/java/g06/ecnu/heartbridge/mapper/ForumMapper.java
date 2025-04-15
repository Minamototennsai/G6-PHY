package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.entity.Forum;
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
 * @since 2025-04-08
 */
@Mapper
public interface ForumMapper extends BaseMapper<Forum> {
    @Select("SELECT DISTINCT f.* " +
            "FROM forum f " +
            "LEFT JOIN comment c1 ON f.id = c1.target_id AND c1.comment_type = 'forum' AND c1.user_id = #{userId} " +
            "LEFT JOIN comment c2 ON c1.id = c2.target_id AND c2.comment_type = 'comment' AND c2.user_id = #{userId} " +
            "WHERE f.creator_id = #{userId} " +
            "OR c1.user_id = #{userId} " +
            "OR c2.user_id = #{userId} " +
            "LIMIT #{page}, #{pageSize}")
    List<Forum> findForumsByUserId(@Param("userId") int userId,
                                   @Param("page") int page,
                                   @Param("pageSize") int pageSize);
}
