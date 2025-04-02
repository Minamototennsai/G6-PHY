package g06.ecnu.heartbridge.mapper;

import g06.ecnu.heartbridge.DTO.ArticleDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleSearchMapperTest {

    @Autowired
    ArticleSearchMapper articleSearchMapper;
    @Test
    void search() {
        ArrayList<Integer>arrayList=new ArrayList<>();
        articleSearchMapper.search(arrayList);
        assertTrue(arrayList.isEmpty());
        arrayList.add(4);
        arrayList.add(2);
        List<ArticleDTO> list=articleSearchMapper.search(arrayList);
        assertEquals(2,list.get(0).getArticle_id());
        assertEquals(2,list.get(1).getArticle_id());

        ArrayList<String> arrayList2=new ArrayList<>();
        arrayList2.add("Ralph Stephens");
        arrayList2.add("Heung Sai Wing");
        String keyword="Mr.";
        list=articleSearchMapper.searchByKeyAndTag(keyword,arrayList2);
        assertEquals(3,list.size());
        assertEquals(2,list.get(0).getArticle_id());
        assertEquals(2,list.get(2).getArticle_id());
    }
}