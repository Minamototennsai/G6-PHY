package g06.ecnu.heartbridge.service;

import g06.ecnu.heartbridge.DTO.*;
import g06.ecnu.heartbridge.cache.ArticleCache;
import g06.ecnu.heartbridge.mapper.*;
import g06.ecnu.heartbridge.pojo.Article;
import g06.ecnu.heartbridge.pojo.ArticleResponseData;
import g06.ecnu.heartbridge.pojo.CreateNewArticleResponse;
import g06.ecnu.heartbridge.utils.ArticleSuggest;
import g06.ecnu.heartbridge.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
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
    private ArticleChangeMapper articleChangeMapper;

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

    private ArticleSuggest initArticleSuggest(UserWithPreferAndArticleHistoryDTO dto){
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
        return suggest;
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
        ArticleSuggest suggest=initArticleSuggest(dto);
        List<ArticleDTO>articleDTOS=articleSearchMapper.searchByKeyAndTag(keyword,tagArray);
        List<Article>articles=integrate(articleDTOS);
        HashMap<Integer,Double>idToScore=new HashMap<>();
        for(Article article:articles){
            idToScore.put(article.getArticle_id(),suggest.getSuggestParam(article.getTags(),article.getLiked_count(),article.getViews_count()));
        }
        articles.sort((a,b)-> {
            return Double.compare(idToScore.get(b.getArticle_id()),idToScore.get(a.getArticle_id()));
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
        }
        ArticleResponseData data=new ArticleResponseData();
        data.setArticles(articles1);
        data.setTotal(articles.size());
        ArticleSearchDTO articleSearchDTO=new ArticleSearchDTO();
        articleSearchDTO.setData(data);
        return ResponseEntity.ok(articleSearchDTO);
    }


    /**
     * 根据articleId查询细节的内容
     * @param articleId 文章id
     * @param request http请求
     * @return 响应体
     */
    public ResponseEntity<NewArticleDetailDTO> getArticleDetail(int articleId, HttpServletRequest request){
        String jwt=request.getHeader("Authorization").substring(7);
        int userId=JwtUtil.validateToken(jwt).get("userId", Integer.class);
        ArticleDetailDTO dto=articleDetailMapper.getArticleDetailById(articleId);
        afterReadArticleUpdateMapper.addOneViewInArticle(articleId);
        afterReadArticleUpdateMapper.addReadLog(userId,articleId);
        NewArticleDetailDTO newArticleDetailDTO=new NewArticleDetailDTO();
        newArticleDetailDTO.setData(dto);
        return ResponseEntity.ok(newArticleDetailDTO);
    }

    /**
     * 根据articleId推送3篇相似的文章
     * @param articleId 文章id
     * @param request http请求
     * @return 响应体
     */
    public ResponseEntity<ArticleSearchDTO> getSimilarArticles(int articleId, HttpServletRequest request){
        String jwt=request.getHeader("Authorization").substring(7);
        int userId=JwtUtil.validateToken(jwt).get("userId", Integer.class);
        UserWithPreferAndArticleHistoryDTO userDTO=userArticleHistoryMapper.getRecord(userId);
        ArticleSuggest suggest=initArticleSuggest(userDTO);
        ArrayList<String> tags=articleDetailMapper.getAllTagByArticleId(articleId);
        suggest.setPreferTags(new HashSet<>(tags));
        List<ArticleDTO>allArticleList=articleSearchMapper.searchByKeyAndTag(null,null);
        ArrayList<Article>allArticles=integrate(allArticleList);
        HashMap<Integer,Double>idToScore=new HashMap<>();
        for(Article article:allArticles){
            idToScore.put(article.getArticle_id(),suggest.getSuggestParam(article.getTags(),article.getLiked_count(),article.getViews_count()));
        }
        allArticles.sort((a,b)-> {
            return Double.compare(idToScore.get(b.getArticle_id()),idToScore.get(a.getArticle_id()));
        });
        ArrayList<Article>suggestArticles=new ArrayList<>();
        for (Article allArticle : allArticles) {
            if (allArticle.getArticle_id() == articleId) {
                continue;
            }
            suggestArticles.add(allArticle);
            if (suggestArticles.size() == 3) break;
        }
        ArticleResponseData responseData=new ArticleResponseData();
        responseData.setArticles(suggestArticles);
        responseData.setTotal(suggestArticles.size());
        ArticleSearchDTO articleSearchDTO=new ArticleSearchDTO();
        articleSearchDTO.setData(responseData);
        return ResponseEntity.ok(articleSearchDTO);
    }

    /**
     * 插入文章于数据库中
     * @param title 文章标题
     * @param content 正文
     * @param tags 标签组
     * @param request http请求
     * @return http响应
     */
    @Transactional
    public ResponseEntity<CreateNewArticleResponseDTO> createArticle(String title,String content,List<String>tags,HttpServletRequest request){
        String jwt=request.getHeader("Authorization").substring(7);
        int userId=JwtUtil.validateToken(jwt).get("userId", Integer.class);
        if(articleChangeMapper.checkId(userId)==0){
            CreateNewArticleResponseDTO dto=new CreateNewArticleResponseDTO();
            dto.setMessage("非咨询师正试图创建文章");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }else{
            Article article=new Article();
            articleChangeMapper.createNewArticle(title,content,userId,article);
            int articleId=article.getArticle_id();
            articleChangeMapper.insertArticleTags(articleId,tags);
            CreateNewArticleResponseDTO dto=new CreateNewArticleResponseDTO();
            dto.setMessage("成功");
            CreateNewArticleResponse response=new CreateNewArticleResponse();
            response.setArticle_id(articleId);
            response.setCreate_time(articleDetailMapper.getArticleDetailById(articleId).getCreate_time());
            dto.setData(response);
            return ResponseEntity.ok(dto);
        }
    }

    @Transactional
    public ResponseEntity<MessageDTO> deleteArticle(int articleId){
        try {
            articleChangeMapper.deleteAllArticleTagById(articleId);
            articleChangeMapper.deleteArticle(articleId);
        }catch (Exception e){
            MessageDTO dto=new MessageDTO();
            dto.setMessage("文章id不存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
        }
        MessageDTO dto=new MessageDTO();
        dto.setMessage("操作成功");
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<NewArticleRecommendDTO> recommendArticle(){
        ArrayList<Integer>list=articleSearchMapper.getAllId();
        Collections.shuffle(list);
        List<Article>articleList=new ArrayList<>();
        for(int i=0;i<list.size()&&i<3;i++){
            ArticleDetailDTO articleDetailDTO=articleDetailMapper.getArticleDetailById(list.get(i));
            Article article=new Article();
            article.setArticle_id(list.get(i));
            article.setPreview(articleDetailDTO.getContent().substring(0,50));
            article.setWriter_name(articleDetailDTO.getWriter_name());
            article.setTitle(articleDetailDTO.getTitle());
            articleList.add(article);
        }
        ArticleRecommendDTO articleRecommendDTO=new ArticleRecommendDTO();
        articleRecommendDTO.setArticles(articleList);
        articleRecommendDTO.setTotal(articleList.size());
        NewArticleRecommendDTO newArticleRecommendDTO=new NewArticleRecommendDTO();
        newArticleRecommendDTO.setData(articleRecommendDTO);
        return ResponseEntity.ok(newArticleRecommendDTO);
    }
}
