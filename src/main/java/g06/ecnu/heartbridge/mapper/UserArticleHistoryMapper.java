package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.UserWithPreferAndArticleHistoryDTO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * 查询某个用户的多级联偏好tag，多级联文章阅读记录tag最近10次访问
 *
 * @author 璃樘鼎臻
 * @since 2025/4/1 下午11:29
 **/
@Mapper
public interface UserArticleHistoryMapper {
    @Select("""
        select id as user_id
        from users
        where id=#{userId}
    """)
    @Results({
            @Result(column = "user_id", property = "userId"),
            @Result(column = "user_id", property = "preferTags", many = @Many(select = "g06.ecnu.heartbridge.mapper.UserArticleHistoryMapper.getPreferTag", fetchType = FetchType.EAGER)),
            @Result(column = "user_id",property = "historyTags",many = @Many(select = "g06.ecnu.heartbridge.mapper.UserArticleHistoryMapper.getHistoryTag",fetchType = FetchType.EAGER))
    })
    public UserWithPreferAndArticleHistoryDTO getRecord(@Param("userId") int userId);

    @Select("""
        select name as prefer_tag
        from preferences_tag join tag on tag_id=tag.id
        where user_id=#{userId}
    """)
    public List<String> getPreferTag(int userId);

    @Select("""
        select name as history_tag
        from ((
            select *
            from read_log
            where user_id=#{userId} and type='article'
            ) as read_next join article_tag on article_id=target_id)join tag on tag_id=tag.id
            order by create_time desc limit 10
    """)
    public List<String> getHistoryTag(int userId);
}
