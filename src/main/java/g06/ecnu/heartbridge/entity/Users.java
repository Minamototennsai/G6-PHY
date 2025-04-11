package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Serial
    private static final long serialVersionUID = 1L;


    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("username")
    private String username;

    private String password;

    private String phone;

    private String email;

    private String profile;

    @JsonProperty("theme_preference")
    private String themePreference;

    private Object status;

    private String type;

    public Users(String username, String password, String phone, String email, String type) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }
}
