package g06.ecnu.heartbridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * Controller
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/22
 */
@Controller
public class VueController {

    @RequestMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

    @RequestMapping("/my/*")
    public String redirectMy() {
        return "forward:/index.html";
    }

    @RequestMapping("/forum/*")
    public String redirectForum() {
        return "forward:/index.html";
    }

    @RequestMapping("/consultation/*")
    public String redirectConsultation() {
        return "forward:/index.html";
    }

    @RequestMapping("/consultant/*")
    public String redirectConsultant() {
        return "forward:/index.html";
    }

    @RequestMapping("/article/*")
    public String redirectArticle() {
        return "forward:/index.html";
    }
}
