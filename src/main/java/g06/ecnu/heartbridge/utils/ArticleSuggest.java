package g06.ecnu.heartbridge.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author 璃樘鼎臻
 * @since 2025/3/31 上午9:46
 * 文章标签，点赞数，浏览数->推荐指数
 * 文章推荐类算法
 **/
@Component
@Scope("prototype")
public class ArticleSuggest {
    /**
     * 用户偏好标签组
     */
    @Setter
    @Getter
    private HashSet<String>preferTags;
    /**
     * 用户历史文章记录
     */
    @Setter
    @Getter
    private Map<String,Integer>history;

    /**
     * 参数表
     */
    private double tagParam=10000.0;
    private double tagPreferParam=1.0;
    private double tagHistoryParam=0.1;
    private double likeParam=10.0;
    private double viewParam=1.0;
    private double randomParam=1000.0;


    /**
     * 调整偏好因子和历史记录因子
     * @param tagPreferParam 偏好因子
     * @param tagHistoryParam 历史记录因子
     */
    public void initTagPrefer(Double tagPreferParam,Double tagHistoryParam){
        this.tagPreferParam=tagPreferParam;
        this.tagHistoryParam=tagHistoryParam;
    }

    /**
     * 调整其它参数
     * @param tagParam 标签因子
     * @param likeParam 点赞因子
     * @param viewParam 浏览数因子
     * @param randomParam 随机因子
     */
    public void initParam(Double tagParam,Double likeParam,Double viewParam,Double randomParam){
        this.tagParam=tagParam;
        this.likeParam=likeParam;
        this.viewParam=viewParam;
        this.randomParam=randomParam;
    }

    /**
     * 计算推荐指数
     * @param articleTags 文章标签组
     * @param like 文章点赞数
     * @param view 文章浏览数
     * @return 推荐指数
     */
    public double getSuggestParam(List<String> articleTags,Integer like,Integer view){
        int p_t=0,h_t=0;
        for(String tag:articleTags){
            if(preferTags.contains(tag)){
                p_t++;
            }
            if(history.containsKey(tag)){
                h_t+=history.get(tag);
            }
        }
        return tagParam*(p_t*tagPreferParam+h_t*tagHistoryParam)+like*likeParam+view*viewParam+randomParam*Math.random();
    }
}
