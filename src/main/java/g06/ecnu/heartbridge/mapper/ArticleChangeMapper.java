package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 对文章进行增删改
 * @author 璃樘鼎臻
 * @since 2025/4/3 下午3:10
 **/
@Mapper
public interface ArticleChangeMapper {
    /**
     * 某个用户创建了一篇文章，加入这篇文章到数据库里
     * @param title 标题
     * @param content 文章内容
     * @param userId 用户id
     */
    @Insert("""
        insert into articles(title, content, writer_id, create_time, view_count, liked_count)
        values (#{title},#{content},#{userId},NOW(),0,0)
    """)
    @Options(useGeneratedKeys = true, keyProperty = "article.article_id")
    public void createNewArticle(@Param("title") String title,@Param("content") String content,@Param("userId") int userId,@Param("article") Article article);



    /**
     * 插入tags组
     * @param articleId 文章id
     * @param tags 标签组
     */
    @Insert("""
        <script>
        insert into article_tag
        values 
        <foreach collection='tags' item='tag' separator=','>
            (#{articleId},#{tag})
        </foreach>
        </script>
    """)
    public void insertArticleTags( @Param("articleId")int articleId,@Param("tags") List<String> tags);


    /**
     * 判断是否是咨询师
     * @param id userId
     * @return 如果是那就正常返回id，如果不是返回0
     */
    @Select("""
        select ifnull((
            select user_id from consultant_detail where user_id=#{id}
        ),0)
    """)
    public Integer checkId(@Param("id") int id);

    @Delete("""
    delete from article_tag
    where article_id=#{articleId}
    """)
    public void deleteAllArticleTagById(int articleId);

    @Delete("""
    delete from articles
    where id=#{articleId}
    """)
    public void deleteArticle(int articleId);
}
