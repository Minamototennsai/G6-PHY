package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.*;
import g06.ecnu.heartbridge.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/8
 */
@Service
public class CommentService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticlesMapper articlesMapper;
    @Resource
    private ForumMapper forumMapper;
    @Resource
    private LikedMapper likedMapper;
    @Autowired
    private UsersMapper usersMapper;

    public ResponseEntity<Object> addComment(int type, int targetId, String content, int userId) {
        switch (type) {
            case 0: {
                QueryWrapper<Forum> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", targetId);
                Forum forum = forumMapper.selectOne(queryWrapper);
                if (forum == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"目标帖子不存在\"}");
                }
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setUserId(userId);
                comment.setTargetId(targetId);
                comment.setCommentType("forum");
                comment.setCreateTime(LocalDateTime.now());
                int result = commentMapper.insert(comment);
                if (result != 0) {
                    ObjectNode response = objectMapper.createObjectNode();
                    ObjectNode data = objectMapper.createObjectNode();
                    data.put("comment_id", comment.getId());
                    data.put("create_time", comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    response.set("data", data);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库异常，请稍后再试\"}");
                }
            }
            case 1: {
                QueryWrapper<Articles> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", targetId);
                Articles article = articlesMapper.selectOne(queryWrapper);
                if (article == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"目标文章不存在\"}");
                }
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setUserId(userId);
                comment.setTargetId(targetId);
                comment.setCommentType("article");
                comment.setCreateTime(LocalDateTime.now());
                int result = commentMapper.insert(comment);
                if (result != 0) {
                    ObjectNode response = objectMapper.createObjectNode();
                    ObjectNode data = objectMapper.createObjectNode();
                    data.put("comment_id", comment.getId());
                    data.put("create_time", comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    response.set("data", data);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库异常，请稍后再试\"}");
                }
            }
            case 2:{
                QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", targetId);
                Comment targetComment = commentMapper.selectOne(queryWrapper);
                if (targetComment == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"目标评论不存在\"}");
                }
                Comment comment = new Comment();
                comment.setContent(content);
                comment.setUserId(userId);
                comment.setTargetId(targetId);
                comment.setCommentType("comment");
                comment.setCreateTime(LocalDateTime.now());
                int result = commentMapper.insert(comment);
                if (result != 0) {
                    ObjectNode response = objectMapper.createObjectNode();
                    ObjectNode data = objectMapper.createObjectNode();
                    data.put("comment_id", comment.getId());
                    data.put("create_time", comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    response.set("data", data);
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库异常，请稍后再试\"}");
                }
            }
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"评论种类无效\"}");
        }
    }

    public ResponseEntity<Object> deleteComment(int targetId, String userType, int userId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", targetId);
        Comment comment = commentMapper.selectOne(queryWrapper);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"评论不存在\"}");
        }
        if (comment.getUserId() != userId) {
            if (!userType.equals("admin")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"权限不足\"}");
            }
        }
        int result = commentMapper.deleteById(comment.getId());
        if (result != 0) {
            return ResponseEntity.ok("{\"message\":\"删除成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"数据库异常，请稍后再试\"}");
        }
    }

    public ResponseEntity<Object> getArticleComments(int articleId,int userId, int page, int pageSize) {
        Page<Comment> commentPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_type", "article")
                .eq("target_id", articleId)
                .orderByAsc("create_time");
        Page<Comment> comments = commentMapper.selectPage(commentPage, queryWrapper);

        ObjectNode response = objectMapper.createObjectNode();
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("total", comments.getTotal());

        List<JsonNode> commentList = new ArrayList<>();
        for (Comment comment : comments.getRecords()) {
            JsonNode commentNode = buildCommentNode(comment, userId);

            List<Comment> replies = getReplies(comment.getId());
            List<JsonNode> replyList = new ArrayList<>();
            for (Comment reply : replies) {
                JsonNode replyNode = buildCommentNode(reply, userId);
                replyList.add(replyNode);
            }

            ((ObjectNode) commentNode).putArray("reply").addAll(replyList);
            commentList.add(commentNode);
        }

        dataNode.putArray("comments").addAll(commentList);
        response.set("data", dataNode);

        return ResponseEntity.ok(response);
    }

    private JsonNode buildCommentNode(Comment comment, Integer userId) {
        ObjectNode commentNode = objectMapper.createObjectNode();
        commentNode.put("id", comment.getId());
        commentNode.put("user_id", comment.getUserId());
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        Users user = usersMapper.selectOne(queryWrapper);
        commentNode.put("user_name", user.getUsername());
        commentNode.put("content", comment.getContent());
        commentNode.put("time", comment.getCreateTime().toString());
        commentNode.put("liked_count", comment.getLikedCount());

        boolean isLiked = checkIfUserLikedComment(comment.getId(), userId);
        commentNode.put("is_liked", isLiked);

        int replyCount = getReplies(comment.getId()).size();
        commentNode.put("reply_count", replyCount);

        return commentNode;
    }

    private boolean checkIfUserLikedComment(int commentId, Integer userId) {
        QueryWrapper<Liked> likedQuery = new QueryWrapper<>();
        likedQuery.eq("target_id", commentId)
                .eq("user_id", userId)
                .eq("type", "comment");
        return likedMapper.selectCount(likedQuery) > 0;
    }

    public ResponseEntity<Object> getForumComments(int forumId, int userId, int page, int pageSize) {
        Page<Comment> commentPage = new Page<>(page, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_type", "forum")
                .eq("target_id", forumId)
                .orderByAsc("create_time");
        Page<Comment> comments = commentMapper.selectPage(commentPage, queryWrapper);

        ObjectNode response = objectMapper.createObjectNode();
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("total", comments.getTotal());

        List<JsonNode> commentList = new ArrayList<>();
        for (Comment comment : comments.getRecords()) {
            JsonNode commentNode = buildCommentNode(comment, userId);
            List<Comment> replies = getReplies(comment.getId());
            List<JsonNode> replyList = new ArrayList<>();
            for (Comment reply : replies) {
                JsonNode replyNode = buildCommentNode(reply, userId);
                replyList.add(replyNode);
            }

            ((ObjectNode) commentNode).putArray("reply").addAll(replyList);
            commentList.add(commentNode);
        }

        dataNode.putArray("comments").addAll(commentList);
        response.set("data", dataNode);

        return ResponseEntity.ok(response);
    }

    private List<Comment> getReplies(int commentId) {
        QueryWrapper<Comment> replyQuery = new QueryWrapper<>();
        replyQuery.eq("comment_type", "comment")
                .eq("target_id", commentId)
                .orderByAsc("create_time");
        return commentMapper.selectList(replyQuery);
    }
}
