package g06.ecnu.heartbridge.listener;

import g06.ecnu.heartbridge.cache.ArticleCache;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 在连接断开时自动清除文章缓存
 *
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午12:35
 **/
@Component
public class ArticleCacheDeleteListener implements HttpSessionListener {
    @Autowired
    private ArticleCache cache;

    /**
     * 清楚文章排序缓存
     * @param se http事件
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session=se.getSession();
        cache.remove(session.getId());
    }
}
