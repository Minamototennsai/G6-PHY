package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ArticleDetailDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ArticleDetailMapperTest {
    @Autowired
    ArticleDetailMapper articleDetailMapper;
    @Test
    void getArticleDetailById() {
        ArticleDetailDTO dto=articleDetailMapper.getArticleDetailById(2);
        assertNotNull(dto);
    }
}