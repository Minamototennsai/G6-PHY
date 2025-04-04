package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ArticleDetailDTO;
import jakarta.persistence.PreUpdate;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.ArrayList;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/2 下午6:14
 * 获取单篇文章的信息
 **/
@Mapper
public interface ArticleDetailMapper {
    /**
     * 找某个文章的全部信息
     * @param id 文章的id
     * @return 文章全部细节信息
     */
    @Select("""
    select title,content,writer_name,create_time,view_count,liked_count,articles.id as a_id
    from articles join (select id,username as writer_name from users)as u on articles.writer_id = u.id
    where articles.id=#{id}
    """)
    @Results({
            @Result(column = "title",property = "title"),
            @Result(column = "content",property = "content"),
            @Result(column = "writer_name",property = "writer_name"),
            @Result(column = "create_time",property = "create_time"),
            @Result(column = "view_count",property = "views_count"),
            @Result(column = "liked_count",property = "liked_count"),
            @Result(column = "a_id",property = "tags",many = @Many(select = "g06.ecnu.heartbridge.mapper.ArticleDetailMapper.getAllTagByArticleId",fetchType = FetchType.EAGER))
    })
    public ArticleDetailDTO getArticleDetailById(@Param("id") int id);

    /**
     * getArticleDetailById的级联查询，用来根据id找全部的tag名
     * @param id 文章id
     * @return tag 数组
     */
    @Select("""
        select tag.name
        from (select * from article_tag where article_id=#{id})as a_t join tag on a_t.tag_id = tag.id
    """)
    public ArrayList<String> getAllTagByArticleId(int id);
}
