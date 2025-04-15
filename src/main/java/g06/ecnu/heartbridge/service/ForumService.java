package g06.ecnu.heartbridge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import g06.ecnu.heartbridge.entity.Forum;
import g06.ecnu.heartbridge.entity.ForumTag;
import g06.ecnu.heartbridge.mapper.ForumMapper;
import g06.ecnu.heartbridge.mapper.ForumTagMapper;
import g06.ecnu.heartbridge.mapper.TagMapper;
import g06.ecnu.heartbridge.mapper.UsersMapper;
import g06.ecnu.heartbridge.pojo.PostForumRequestBody;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Service
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/4/10
 */
@Service
public class ForumService {
    @Resource
    private ForumMapper forumMapper;
    @Resource
    private ForumTagMapper forumTagMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private UsersMapper usersMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Object> getForums(int page, String keyword, String tags) {
        QueryWrapper<Forum> forumQueryWrapper = new QueryWrapper<>();
        Page<Forum> forumPage = new Page<>(page, 10);
        if (keyword != null && !keyword.isEmpty()) {
            forumQueryWrapper.like("title", keyword)
                    .or().like("content", keyword);
        }
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags.split(",")) {
                QueryWrapper<ForumTag> forumTagQueryWrapper = new QueryWrapper<>();
                forumTagQueryWrapper.eq("tag_id", Integer.parseInt(tag));
                List<ForumTag> forumTags = forumTagMapper.selectList(forumTagQueryWrapper);
                if (!forumTags.isEmpty()) {
                    for (ForumTag forumTag : forumTags) {
                        forumQueryWrapper.or().eq("id", forumTag.getForumId());
                    }
                }
            }
        }
        forumPage = forumMapper.selectPage(forumPage, forumQueryWrapper);
        List<Forum> forums = forumPage.getRecords();

        if (!forums.isEmpty()) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            ArrayNode forumArray = objectMapper.createArrayNode();
            for (Forum forum : forums) {
                ObjectNode forumNode = objectMapper.createObjectNode();
                forumNode.put("id", forum.getId());
                forumNode.put("title", forum.getTitle());
                forumNode.put("preview", forum.getContent().substring(0,20));
                ArrayNode tagArray = objectMapper.valueToTree(tagMapper.getForumTags(forum.getId()));
                forumNode.set("tags", tagArray);
                ObjectNode creatorNode = objectMapper.createObjectNode();
                creatorNode.put("id", forum.getCreatorId());
                creatorNode.put("username", usersMapper.selectById(forum.getCreatorId()).getUsername());
                forumNode.set("creator", creatorNode);
                forumNode.put("create_time", forum.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumNode.put("update_time", forum.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumArray.add(forumNode);
            }
            data.put("total", forumMapper.selectCount(null));
            data.set("forums", forumArray);
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到相关论坛\"}");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Object> addForum(int userId, PostForumRequestBody body) {
        try {
            Forum forum = new Forum();
            forum.setCreatorId(userId);
            forum.setTitle(body.getTitle());
            forum.setContent(body.getContent());
            forum.setCreateTime(LocalDateTime.now());
            forum.setUpdateTime(LocalDateTime.now());
            forumMapper.insert(forum);
            if (body.getTags() != null && !body.getTags().isEmpty()) {
                for (String tag : body.getTags()) {
                    ForumTag forumTag = new ForumTag();
                    forumTag.setForumId(forum.getId());
                    forumTag.setTagId(Integer.parseInt(tag));
                    forumTagMapper.insert(forumTag);
                }
            }
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            ObjectNode forumNode = objectMapper.createObjectNode();
            forumNode.put("id", forum.getId());
            forumNode.put("title", forum.getTitle());
            ObjectNode creatorNode = objectMapper.createObjectNode();
            creatorNode.put("id", forum.getCreatorId());
            creatorNode.put("username", usersMapper.selectById(forum.getCreatorId()).getUsername());
            forumNode.set("creator", creatorNode);
            forumNode.put("create_time", forum.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            data.set("forum", forumNode);
            response.set("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"发帖失败，请稍后再试\"}");
        }
    }

    public ResponseEntity<Object> getForum(int id) {
        Forum forum = forumMapper.selectById(id);
        if (forum != null) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            ObjectNode forumNode = objectMapper.createObjectNode();
            forumNode.put("id", forum.getId());
            forumNode.put("title", forum.getTitle());
            forumNode.put("content", forum.getContent());
            ObjectNode creatorNode = objectMapper.createObjectNode();
            creatorNode.put("id", forum.getCreatorId());
            creatorNode.put("username", usersMapper.selectById(forum.getCreatorId()).getUsername());
            forumNode.set("creator", creatorNode);
            ArrayNode tagArray = objectMapper.valueToTree(tagMapper.getForumTags(forum.getId()));
            forumNode.set("tags", tagArray);
            forumNode.put("create_time", forum.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            forumNode.put("update_time", forum.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            data.set("forum", forumNode);
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到论坛\"}");
        }
    }

    public ResponseEntity<Object> getJoinedForums(int userId, int page) {
        List<Forum> forums = forumMapper.findForumsByUserId(userId, page-1, 10);
        if (!forums.isEmpty()) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            ArrayNode forumArray = objectMapper.createArrayNode();
            for (Forum forum : forums) {
                ObjectNode forumNode = objectMapper.createObjectNode();
                forumNode.put("id", forum.getId());
                forumNode.put("title", forum.getTitle());
                forumNode.put("preview", forum.getContent().substring(0,20));
                ArrayNode tagArray = objectMapper.valueToTree(tagMapper.getForumTags(forum.getId()));
                forumNode.set("tags", tagArray);
                ObjectNode creatorNode = objectMapper.createObjectNode();
                creatorNode.put("id", forum.getCreatorId());
                creatorNode.put("username", usersMapper.selectById(forum.getCreatorId()).getUsername());
                forumNode.set("creator", creatorNode);
                forumNode.put("create_time", forum.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumNode.put("update_time", forum.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumArray.add(forumNode);
            }
            data.put("total", forums.size());
            data.set("forums", forumArray);
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到相关论坛\"}");
        }
    }

    public ResponseEntity<Object> getPostedForums(int userId, int page) {
        QueryWrapper<Forum> forumQueryWrapper = new QueryWrapper<>();
        Page<Forum> forumPage = new Page<>(page, 10);
        forumQueryWrapper.eq("creator_id", userId);
        forumPage = forumMapper.selectPage(forumPage, forumQueryWrapper);
        List<Forum> forums = forumPage.getRecords();
        if (!forums.isEmpty()) {
            ObjectNode response = objectMapper.createObjectNode();
            ObjectNode data = objectMapper.createObjectNode();
            ArrayNode forumArray = objectMapper.createArrayNode();
            for (Forum forum : forums) {
                ObjectNode forumNode = objectMapper.createObjectNode();
                forumNode.put("id", forum.getId());
                forumNode.put("title", forum.getTitle());
                forumNode.put("preview", forum.getContent().substring(0,20));
                ArrayNode tagArray = objectMapper.valueToTree(tagMapper.getForumTags(forum.getId()));
                forumNode.set("tags", tagArray);
                ObjectNode creatorNode = objectMapper.createObjectNode();
                creatorNode.put("id", forum.getCreatorId());
                creatorNode.put("username", usersMapper.selectById(forum.getCreatorId()).getUsername());
                forumNode.set("creator", creatorNode);
                forumNode.put("create_time", forum.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumNode.put("update_time", forum.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                forumArray.add(forumNode);
            }
            data.put("total", forums.size());
            data.set("forums", forumArray);
            response.set("data", data);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到相关论坛\"}");
        }
    }

    public ResponseEntity<Object> deleteForum(int id, String userType, int userId) {
        Forum forum = forumMapper.selectById(id);
        if (forum == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"未找到相关论坛\"}");
        }
        if (forum.getCreatorId() != userId && !Objects.equals(userType, "admin")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"您没有权限\"}");
        }
        int result = forumMapper.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok("{\"message\":\"删除成功\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"删除失败，请稍后再试\"}");
        }
    }
}
