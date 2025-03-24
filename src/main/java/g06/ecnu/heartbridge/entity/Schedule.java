package g06.ecnu.heartbridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
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
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private Integer clientId;

    private Integer consultantId;

    private LocalDate date;

    private Integer time;

    private Object agree;

    public Schedule(Integer clientId, Integer consultantId, LocalDate date, int time) {
        this.clientId = clientId;
        this.consultantId = consultantId;
        this.date = date;
        this.time = time;
    }
}
