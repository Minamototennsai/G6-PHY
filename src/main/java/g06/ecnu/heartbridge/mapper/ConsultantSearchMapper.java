package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.pojo.ConsultantDetail;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

/**
 * 获取所有咨询师
 *
 * @author 璃樘鼎臻
 * @since 2025/4/3 下午11:03
 **/
@Mapper
public interface ConsultantSearchMapper {
    @Select("""
        select user_id,avg_score,'1' as context,username,tag_name
        from (consultant_detail join (select id,username from users)as u on consultant_detail.user_id=u.id)join (select user_id as u_id,name as tag_name from expertise_tag join tag on expertise_tag.tag_id = tag.id)as u_tn on u_tn.u_id=consultant_detail.user_id
    """)
    @Results({
            @Result(column ="user_id",property = "id"),
            @Result(column = "username",property = "username"),
            @Result(column = "context",property = "profile"),
            @Result(column = "avg_score",property = "avgScore"),
            @Result(column = "tag_name",property = "tag")
    })
    public List<ConsultantDetail> getAllConsultants();
}
