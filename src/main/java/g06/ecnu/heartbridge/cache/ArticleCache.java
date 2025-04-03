package g06.ecnu.heartbridge.cache;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文章缓存
 *
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午12:17
 **/
@Component
public class ArticleCache {
    private final ConcurrentHashMap<String, String>sessionId2keyword=new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, HashSet<String>> sessionId2tags =new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ArrayList<Integer>> sessionId2rankMap = new ConcurrentHashMap<>();

    public void save(String sessionId,String keyword, HashSet<String> tags, ArrayList<Integer> rank) {
        sessionId2keyword.put(sessionId, keyword);
        sessionId2rankMap.put(sessionId, rank);
        sessionId2tags.put(sessionId, tags);
    }
    public void remove(String sessionId) {
        sessionId2keyword.remove(sessionId);
        sessionId2rankMap.remove(sessionId);
        sessionId2tags.remove(sessionId);
    }

    /**
     * 返回会话id对应的上一次查询的标题
     * @param sessionId 会话id
     * @return 标题
     */
    public String getKeyword(String sessionId) {
        return sessionId2keyword.get(sessionId);
    }

    /**
     * 返回会话id对应的上一次查询
     * @param sessionId 会话id
     * @return 查询排序
     */
    public ArrayList<Integer> getRank(String sessionId) {
        return sessionId2rankMap.get(sessionId);
    }

    /**
     * 返回会话id对应的上一次查询的关键字组
     * @param sessionId 会话id
     * @return 关键字组
     */
    public HashSet<String> getTags(String sessionId) {
        return sessionId2tags.get(sessionId);
    }
}
