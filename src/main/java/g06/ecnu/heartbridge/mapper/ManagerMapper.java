package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ReportByIdContentDTO;
import g06.ecnu.heartbridge.DTO.ReportByIdUserDTO;
import g06.ecnu.heartbridge.pojo.ConsultantCertificatedInfo;
import g06.ecnu.heartbridge.pojo.IdAndContent;
import g06.ecnu.heartbridge.pojo.Report;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 璃樘鼎臻
 * @since 2025/4/10 上午9:46
 **/
@Mapper
public interface ManagerMapper {
    @Select("""
        select c.user_id ,u.username,c.certification
        from (select id,username from users)as u join (select user_id,consultant_detail.certification from consultant_detail where is_certificated='no') as c on id=user_id
    """)
    @Results({
            @Result(column = "user_id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "certification", property = "authentication_code")
    })
    List<ConsultantCertificatedInfo> getAllHaveNotBeenCertificatedConsultant();

    @Delete("""
        delete from consultant_detail
        where user_id  =#{id} and is_certificated='no';
    """)
    void deleteConsultantById(@Param("id") int id);

    @Delete("""
    delete from users where id = #{id};
    """)
    void deleteUserById(@Param("id") int id);

    @Update("""
        update consultant_detail
        set is_certificated=#{approve}
        where user_id=#{id};
    """)
    void updateConsultantById(@Param("id") int id, @Param("approve")String approve);

    @Select("""
        select * from report
    """)
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "sender_id",property = "sender_id"),
            @Result(column = "type",property = "type"),
            @Result(column = "target_id",property = "target_id")
    })
    ArrayList<Report> getAllReport();

    @Delete("""
    delete from report
    where id=#{id}
    """)
    void deleteReportById(@Param("id") int id);


    @Update("""
    update users
    set status='inactive'
    where id=#{id}
    """)
    void banUser(@Param("id") int id);

    @Update("""
    update users
    set status='active'
    where id=#{id}
    """)
    void unbanUser(@Param("id") int id);

    @Select("""
    select id,users.username
    from users
    where id=#{id}
    """)
    @Results({
            @Result(column = "id",property = "id"),
            @Result(column = "username",property = "name")
    })
    ReportByIdUserDTO searchUserById(@Param("id") int id);


    @Select("""
    select comment.user_id,comment.content
    from comment
    where id=#{id}
    """)
    @Results({
            @Result(column = "user_id",property = "user_id"),
            @Result(column = "content",property = "content")
    })
    ReportByIdContentDTO searchEvaluateById(@Param("id") int id);

    @Select("""
    select articles.writer_id,articles.content
    from articles
    where id=#{id}
    """)
    @Results({
            @Result(column = "writer_id",property = "user_id"),
            @Result(column = "content",property = "content")
    })
    ReportByIdContentDTO searchArticleById(@Param("id")int id);

    @Select("""
    select forum.creator_id,forum.content
    from forum
    where id=#{id}
    """)
    @Results({
            @Result(column = "creator_id",property = "user_id"),
            @Result(column = "content",property = "content")
    })
    ReportByIdContentDTO searchForumById(@Param("id")int id);


    @Select("""
    select chat_message.sender_id,chat_message.content
    from chat_message
    where session_id=#{id}
    """)
    @Results({
            @Result(column = "sender_id",property = "id"),
            @Result(column = "content",property = "content")
    })
    ArrayList<IdAndContent> searchAllSessionLogsById(@Param("id") int id);


}
