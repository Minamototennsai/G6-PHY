package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.pojo.ConsultantCertificatedInfo;
import org.apache.ibatis.annotations.*;

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
}
