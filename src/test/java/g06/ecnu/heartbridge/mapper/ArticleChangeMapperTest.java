package g06.ecnu.heartbridge.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ArticleChangeMapperTest {
    @Autowired
    ArticleChangeMapper articleChangeMapper;
    @Test
    void createNewArticle() {
    }

    @Test
    void checkId() {
        assertEquals(1,articleChangeMapper.checkId(1));
        assertEquals(0,articleChangeMapper.checkId(1432334));
    }

    @Test
    void insertArticleTags() {
        List<String> list=new ArrayList<>();
        list.add("5");
        articleChangeMapper.insertArticleTags(1,list);
    }
}