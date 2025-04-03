package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.ArticleDTO;
import g06.ecnu.heartbridge.DTO.ArticleDetailDTO;
import g06.ecnu.heartbridge.DTO.ArticleSearchDTO;
import g06.ecnu.heartbridge.DTO.UserWithPreferAndArticleHistoryDTO;
import g06.ecnu.heartbridge.cache.ArticleCache;
import g06.ecnu.heartbridge.mapper.AfterReadArticleUpdateMapper;
import g06.ecnu.heartbridge.mapper.ArticleDetailMapper;
import g06.ecnu.heartbridge.mapper.ArticleSearchMapper;
import g06.ecnu.heartbridge.mapper.UserArticleHistoryMapper;
import g06.ecnu.heartbridge.pojo.Article;
import g06.ecnu.heartbridge.pojo.ArticleResponseData;
import g06.ecnu.heartbridge.utils.ArticleSuggest;
import g06.ecnu.heartbridge.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 璃樘鼎臻
 * @since 2025/3/31 下午5:22
 **/
@Service
public class ArticleService {

    @Autowired
    private ArticleSearchMapper articleSearchMapper;

    @Autowired
    private UserArticleHistoryMapper userArticleHistoryMapper;

    @Autowired
    private ArticleDetailMapper articleDetailMapper;

    @Autowired
    private AfterReadArticleUpdateMapper afterReadArticleUpdateMapper;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private ArticleCache cache;

    private ArrayList<Article> integrate(List<ArticleDTO> list){
        HashMap<Integer,Article>map=new HashMap<>();
        for(ArticleDTO articleDTO:list){
            Integer articleId=articleDTO.getArticle_id();
            if(map.containsKey(articleId)){
                map.get(articleId).getTags().add(articleDTO.getTag());
            }else {
                Article article=new Article();
                article.setArticle_id(articleId);
                article.setTitle(articleDTO.getTitle());
                article.setTags(new ArrayList<>());
                article.getTags().add(articleDTO.getTag());
                article.setPreview(articleDTO.getPreview());
                article.setLiked_count(articleDTO.getLiked_count());
                article.setCreate_time(articleDTO.getCreate_time());
                article.setViews_count(articleDTO.getView_count());
                article.setWriter_name(articleDTO.getWriter_name());
                map.put(articleId,article);
            }
        }
        return new ArrayList<>(map.values());
    }

    private ArrayList<Article> ranking(List<Article> articles, List<Integer> rank){
        HashMap<Integer,Article>map=new HashMap<>();
        for(Article article:articles){
            Integer articleId=article.getArticle_id();
            map.put(articleId,article);
        }
        ArrayList<Article> result=new ArrayList<>();
        for (Integer integer : rank) {
            result.add(map.get(integer));
        }
        return result;
    }

    /**
     * 按照关键字，标签组搜索文章，按照推荐算法排序好，返回一页
     * 如果keyword和tags已经在cache中存储，那么直接从缓存中拿出
     * @param keyword 关键字
     * @param page 页码
     * @param tags 标签组
     * @param session 会话，自动注入
     * @return http 响应
     */
    public ResponseEntity<ArticleSearchDTO> searchArticles(String keyword, Integer page, String[] tags, HttpSession session, HttpServletRequest request) {
        if(keyword==null)keyword="";
        if(page==null||page==0)page=1;
        List<String> tagArray=new ArrayList<>();
        if(tags!=null)tagArray= Arrays.asList(tags);
        ArrayList<Integer> result;
        if(cache.getKeyword(session.getId())!=null&&cache.getTags(session.getId())!=null){
            if(keyword.equals(cache.getKeyword(session.getId()))){
                HashSet<String> temp=cache.getTags(session.getId());
                boolean p=true;
                for(String str:tagArray){
                    if(!temp.contains(str)){
                        p=false;
                        break;
                    }
                }
                if(temp.size()!=tagArray.size()){
                    p=false;
                }
                if(p){//在缓存中
                    result=cache.getRank(session.getId());
                    ArrayList<Integer> temp2=new ArrayList<>();
                    for(int i=(page-1)*10;i<page*10&&i<result.size();i++){
                        temp2.add(result.get(i));
                    }
                    List<ArticleDTO>list=articleSearchMapper.search(temp2);
                    ArrayList<Article> articles=integrate(list);
                    articles=ranking(articles,temp2);
                    ArticleResponseData data=new ArticleResponseData();
                    data.setArticles(articles);
                    data.setTotal(articles.size());
                    ArticleSearchDTO articleSearchDTO=new ArticleSearchDTO();
                    articleSearchDTO.setData(data);
                    return ResponseEntity.ok(articleSearchDTO);
                }
            }
        }
        UserWithPreferAndArticleHistoryDTO dto;
        try{
            String jwt=request.getHeader("Authorization").substring(7);
            int userId= JwtUtil.validateToken(jwt).get("userId", Integer.class);

            dto=userArticleHistoryMapper.getRecord(userId);
        }catch (Exception e){
            dto=new UserWithPreferAndArticleHistoryDTO();
            dto.setPreferTags(new ArrayList<>());
            dto.setHistoryTags(new ArrayList<>());
            dto.setUserId(0);
        }
        ArticleSuggest suggest= factory.getBean(ArticleSuggest.class);
        suggest.setPreferTags(new HashSet<>(dto.getPreferTags()));
        HashMap<String,Integer>map=new HashMap<>();
        for(int i=0;i<dto.getHistoryTags().size();i++){
            String str=dto.getHistoryTags().get(i);
            if(map.containsKey(str)){
                map.put(str,map.get(str)+1);
            }else {
                map.put(str,1);
            }
        }
        suggest.setHistory(map);
        List<ArticleDTO>articleDTOS=articleSearchMapper.searchByKeyAndTag(keyword,tagArray);
        List<Article>articles=integrate(articleDTOS);
        articles.sort((a,b)->{
            if(suggest.getSuggestParam(a.getTags(),a.getLiked_count(),a.getViews_count())-suggest.getSuggestParam(b.getTags(),b.getLiked_count(),b.getViews_count())>0){
                return -1;
            }else if(suggest.getSuggestParam(a.getTags(),a.getLiked_count(),a.getViews_count())-suggest.getSuggestParam(b.getTags(),b.getLiked_count(),b.getViews_count())==0){
                return 0;
            }else {
                return 1;
            }
        });
        //将查询结果放进缓存
        ArrayList<Integer>ranks=new ArrayList<>();
        for (Article article : articles) {
            ranks.add(article.getArticle_id());
        }
        cache.save(session.getId(),keyword,new HashSet<>(tagArray),ranks);

        ArrayList<Article> articles1=new ArrayList<>();
        for(int i=(page-1)*10;i<page*10&&i<articles.size();i++){
            articles1.add(articles.get(i));
            articles1.get(i).setPreview(articles1.get(i).getPreview().substring(0,30));
        }
        ArticleResponseData data=new ArticleResponseData();
        data.setArticles(articles1);
        data.setTotal(articles1.size());
        ArticleSearchDTO articleSearchDTO=new ArticleSearchDTO();
        articleSearchDTO.setData(data);
        return ResponseEntity.ok(articleSearchDTO);
    }




    public ResponseEntity<ArticleDetailDTO> getArticleDetail(int articleId, HttpServletRequest request){
        String jwt=request.getHeader("Authorization").substring(7);
        int userId=JwtUtil.validateToken(jwt).get("userId", Integer.class);
        ArticleDetailDTO dto=articleDetailMapper.getArticleDetailById(articleId);
        afterReadArticleUpdateMapper.addOneViewInArticle(articleId);
        afterReadArticleUpdateMapper.addReadLog(userId,articleId);
        return ResponseEntity.ok(dto);
    }
}
