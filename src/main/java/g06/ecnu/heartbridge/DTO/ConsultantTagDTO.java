package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用来返回咨询师和标签的数据结构
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/24
 */
@Data
public class ConsultantTagDTO {
    private int id;
    private String username;
    private String profile;
    private List<String> tags;
    private boolean isAvailable;
}
