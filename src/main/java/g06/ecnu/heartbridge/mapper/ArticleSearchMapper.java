package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ArticleDTO;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询全部文章
 *
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午9:23
 **/
@Mapper
public interface ArticleSearchMapper {

    /**
     * 查询某些id的文章
     * @param articleIds id组 如果你给了个空的或者null那就返回一个内容为空的数组对象
     * @return 文章组
     */
    @Select("""
    <script>
        select article_id,title,u.username as writer_name,substr(content,0,30) as preview,view_count,liked_count,create_time,tag.name as tag
        from ((articles as a join article_tag as t on a.id = t.article_id) join (select id,users.username from users) as u on u.id=a.writer_id) join tag on t.tag_id=tag.id
        <if test="articleIds.size()!=null and articleIds.size()!=0">
            where article_id in 
            <foreach collection='articleIds' item='item' open='(' separator=',' close=')'>
                #{item}
            </foreach>
        </if>
        <if test="articleIds.size()==null or articleIds.size()==0">
            where 1=2
        </if>
    </script>
    """)
    @Results({
            @Result(property = "article_id", column = "article_id"),
            @Result(property = "view_count", column = "view_count"),
            @Result(property = "liked_count", column = "liked_count"),
            @Result(property = "writer_name", column = "writer_name"),
            @Result(property = "preview", column = "preview"),
            @Result(property = "create_time", column = "create_time"),
            @Result(property = "title",column = "title"),
            @Result(property = "tag", column = "tag")
    })
    public List<ArticleDTO> search(@Param("articleIds") List<Integer> articleIds);

    /**
     * 找含有keyword段，带有tags里全部标签的文章
     * 如果keyword是空的那就不限制标题，如果tags是空的那就不限制标签。
     * @param keyword 关键字
     * @param tags 标签组
     * @return 结果文章标签数组
     */
    @Select("""
    <script>
        select article_id,title,u.username as writer_name,substr(content,0,30) as preview,view_count,liked_count,create_time,tag.name as tag
        from (((select * from articles
                     <if test="keyword!=null and keyword.length()>0">
                        where title like concat('%',#{keyword},'%')
                     </if>
                 ) as a join article_tag as t on a.id = t.article_id) join (select id,users.username from users) as u on u.id=a.writer_id) join (
            select *
            from tag
            <if test="tags!=null and tags.size()>0">
                where tag.name in
                <foreach collection="tags" item="tg" open="(" separator="," close=")">
                #{tg}
                </foreach>
            </if>
        ) as tag on t.tag_id=tag.id
    </script>
    """)
    @Results({
            @Result(property = "article_id", column = "article_id"),
            @Result(property = "view_count", column = "view_count"),
            @Result(property = "liked_count", column = "liked_count"),
            @Result(property = "writer_name", column = "writer_name"),
            @Result(property = "preview", column = "preview"),
            @Result(property = "create_time", column = "create_time"),
            @Result(property = "title",column = "title"),
            @Result(property = "tag", column = "tag")
    })
    public List<ArticleDTO> searchByKeyAndTag(@Param("keyword")String keyword,@Param("tags")List<String> tags);




    /**
     * 拿到所有id
     * @return id组
     */
    @Select("""
        select id
        from articles
    """)
    public ArrayList<Integer> getAllId();
}
