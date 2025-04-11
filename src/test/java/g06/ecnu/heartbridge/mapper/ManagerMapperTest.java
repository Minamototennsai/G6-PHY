package g06.ecnu.heartbridge.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ManagerMapperTest {
    @Autowired
    private ManagerMapper mapper;
    @Test
    void getAllHaveNotBeenCertificatedConsultant() {
        System.out.println(mapper.getAllHaveNotBeenCertificatedConsultant());
    }
}