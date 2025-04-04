package g06.ecnu.heartbridge.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import g06.ecnu.heartbridge.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-19
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}
