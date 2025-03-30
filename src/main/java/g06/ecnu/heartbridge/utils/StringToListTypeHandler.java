package g06.ecnu.heartbridge.utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 用于将数据库返回的聚类数据拆解成字符串数组并映射
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/25
 */
public class StringToListTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        // 在存入数据库时，把 List<String> 转换成字符串 "a,b,c"
        ps.setString(i, String.join(",", parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 读取数据库时，把 "a,b,c" 转换成 List<String>
        String text = rs.getString(columnName);
        return text == null ? null : Arrays.asList(text.split(","));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String text = rs.getString(columnIndex);
        return text == null ? null : Arrays.asList(text.split(","));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String text = cs.getString(columnIndex);
        return text == null ? null : Arrays.asList(text.split(","));
    }
}
