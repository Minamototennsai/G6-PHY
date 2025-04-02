package g06.ecnu.heartbridge.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/2 下午8:57
 **/
@Mapper
public interface AfterReadArticleUpdateMapper {

    /**
     * 插入一条记录：userId读了articleId文章
     * @param userId 用户id
     * @param articleId 文章id
     */
    @Insert("""
        insert into read_log(user_id, type, target_id, create_time)
        values (#{userId},'article',#{articleId},NOW())
    """)
    public void addReadLog(int userId,int articleId);

    /**
     * 指定文章的阅读记录+1
     * @param articleId 文章id
     */
    @Update("""
        update articles
        set view_count=view_count+1
        where id=#{articleId}
    """)
    public void addOneViewInArticle(int articleId);
}
