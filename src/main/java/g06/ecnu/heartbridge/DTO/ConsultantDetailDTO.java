package g06.ecnu.heartbridge.DTO;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 用来返回咨询师详细信息的数据结构
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/24
 */
@Data
public class ConsultantDetailDTO {
    private int id;
    private String username;
    private List<String> tags;
    private String profile;
    private double avgScore;
}
