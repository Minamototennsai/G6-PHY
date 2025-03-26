package g06.ecnu.heartbridge.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.*;


/**
 * @author 璃樘鼎臻
 * @since 2025/3/19 上午9:07
 * 标签-评分->推荐参数计算器
 * initSuggest是必须的，然后才能调用getSuggestParam
 **/
@Component
@Scope("prototype")
public class ConsultantSuggest {
    /**
     * 咨询者偏好标签组
     */
    @Setter
    @Getter
    private HashSet<String> tags;
    /**
     * 咨询者历史记录<标签，访问次数>
     */
    @Getter
    private Map<String,Integer> histories;
    /**
     * 评分对总分的影响因子
     */
    private Double evaluateImpParam=1.0;
    /**
     * 标签对总分的影响因子
     */
    private Double tagImpParam=1.0;
    /**
     * 用户选择的偏好对标签分的影响因子
     */
    private Double userChoose=1.0;
    private Double userHistory=1.0;
    private Double random=10.0;
    public ConsultantSuggest() {}

    /**
     *初始化咨询者信息，必须
     * @param tags 咨询者偏好标签组
     * @param histories 咨询者历史记录<标签，访问次数>
     */
    public void initSuggest(HashSet<String> tags, Map<String,Integer> histories) {
        this.tags = tags;
        this.histories = histories;
    }

    /**
     * 调整评价和标签对推荐的影响
     * @param evaluateImpParam 评价影响因子
     * @param tagImpParam 标签影响因子
     */
    public void initParam(Double evaluateImpParam, Double tagImpParam) {
        this.evaluateImpParam = evaluateImpParam;
        this.tagImpParam = tagImpParam;
    }

    /**
     * 调整标签影响中手动和历史记录的影响占比
     * @param userChoose 选择偏好标签影响
     * @param userHistory 历史标签影响
     */
    public void initTagPrefer(Double userChoose, Double userHistory) {
        this.userChoose = userChoose;
        this.userHistory = userHistory;
    }

    /**
     * 调整随机因子的占比
     * @param random 随机影响
     */
    public void initRandom(Double random) {
        this.random = random;
    }

    /**
     * 按照公式医生推送分=（偏好匹配数*偏好因子+历史标签占比浓度*历史因子）*标签因子+评分*评分因子+随机因子*[0,1）计算推荐指数
     * @param consultantTags 医生擅长标签组
     * @param consultantAvrEvaluate 医生平均评分
     * @return 推荐指数
     */
    public Double getSuggestParam(List<String>consultantTags,Double consultantAvrEvaluate){
        if(tags==null||histories==null){
            System.out.println("必要参数:咨询者偏好tag或历史记录未提供");
            return 0.0;
        }
        int choose =0;
        int history=0;
        for(String tag:consultantTags){
            if(tags.contains(tag)){
                choose++;
            }
            if(histories.containsKey(tag)){
                history+=histories.get(tag);
            }
        }
        return (choose*userChoose+history*userHistory)*tagImpParam+consultantAvrEvaluate*evaluateImpParam+random*Math.random();
    }

    public void setHistories(TreeMap<String, Integer> histories) {
        this.histories = histories;
    }
}