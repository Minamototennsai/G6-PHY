package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.DTO.*;
import g06.ecnu.heartbridge.pojo.Article;
import g06.ecnu.heartbridge.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章功能控制器
 *
 * @author 璃樘鼎臻
 * @since 2025/3/30 下午9:06
 **/
@Controller
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @GetMapping("")
    public ResponseEntity<ArticleSearchDTO> searchArticles(@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page, @RequestParam(required = false) String[] tags, HttpSession session, HttpServletRequest request) {
        return articleService.searchArticles(keyword, page, tags, session,request);
    }

    @GetMapping("/{article_id}")
    public ResponseEntity<NewArticleDetailDTO> getArticleDetail(@PathVariable("article_id") int articleId, HttpServletRequest request) {
        return articleService.getArticleDetail(articleId,request);
    }

    @GetMapping("/{article_id}/similar")
    public ResponseEntity<ArticleSearchDTO> getSimilarArticles(@PathVariable("article_id") int articleId, HttpServletRequest request) {
        return articleService.getSimilarArticles(articleId,request);
    }

    @PostMapping("")
    public ResponseEntity<CreateNewArticleResponseDTO> createArticle(@RequestBody CreateArticleRequest createArticleRequest, HttpServletRequest request) {
        return articleService.createArticle(createArticleRequest.getTitle(),createArticleRequest.getContent(),createArticleRequest.getTags(),request);
    }

    @DeleteMapping("/{article_id}")
    public ResponseEntity<MessageDTO> deleteArticle(@PathVariable("article_id") int articleId) {
        return articleService.deleteArticle(articleId);
    }

    @GetMapping("/recommended")
    public ResponseEntity<NewArticleRecommendDTO> recommendArticle() {
        return articleService.recommendArticle();
    }
}
