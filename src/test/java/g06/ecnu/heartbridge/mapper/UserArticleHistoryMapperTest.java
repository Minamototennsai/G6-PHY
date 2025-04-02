package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.UserWithPreferAndArticleHistoryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserArticleHistoryMapperTest {
    @Autowired
    UserArticleHistoryMapper mapper;
    @Test
    void getRecord() {
        UserWithPreferAndArticleHistoryDTO dto=mapper.getRecord(1);
        assertEquals(1,dto.getUserId());
    }
}