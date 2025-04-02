package g06.ecnu.heartbridge.controller;

import g06.ecnu.heartbridge.DTO.ArticleSearchDTO;
import g06.ecnu.heartbridge.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
