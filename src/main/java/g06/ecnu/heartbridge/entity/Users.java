package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *  Entity
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025-03-19
 */
@Getter
@Setter
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String phone;

    private String email;

    private String profile;

    private String themePreference;

    private Object status;

    private Object type;

    public Users(String username, String password, String phone, String email, int type) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }
}
