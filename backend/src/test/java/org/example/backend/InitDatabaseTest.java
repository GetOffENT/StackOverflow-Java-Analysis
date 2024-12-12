package org.example.backend;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.backend.crawler.DataCrawler;
import org.example.backend.entity.*;
import org.example.backend.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-11 22:42
 */
@SpringBootTest(classes = BackendApplication.class)
public class InitDatabaseTest {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private QuestionTagMapper questionTagMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DataCrawler dataCrawler;

    private static final String QUESTION_JSON = "data/question.json";

    private static final String ANSWER_JSON = "data/answer.json";

    private static final String COMMENT_JSON = "data/comment.json";


    @Test
    public void test(){
        dataCrawler.getQuestionCount();
        dataCrawler.getQuestionsWithAnswersAndComments();
    }

    @Test
    public void insertQuestionsFromJson() throws IOException {
        // 通过 ClassLoader 获取资源文件流
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(QUESTION_JSON);
        if (inputStream == null) {
            throw new IOException("Unable to find the JSON file at " + QUESTION_JSON);
        }

        // 读取文件内容
        String jsonContent = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        inputStream.close();

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(jsonContent);
        JSONArray items = jsonObject.getJSONArray("items");

        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            // 插入 user 数据
            User owner = insertUser(item.getJSONObject("owner"));

            // 插入 question 数据
            Question question = insertQuestion(item, owner);

            // 插入 tag 数据并处理关联
            insertTags(item.getJSONArray("tags"), question.getQuestionId());
        }
    }

    @Test
    public void insertAnswersFromJson() throws IOException {
        // 读取 JSON 文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ANSWER_JSON);
        if (inputStream == null) {
            throw new IOException("Unable to find the JSON file at " + QUESTION_JSON);
        }

        // 读取文件内容
        String jsonContent = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        inputStream.close();

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(jsonContent);
        JSONArray items = jsonObject.getJSONArray("items");

        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            // 插入 user 数据
            User owner = insertUser(item.getJSONObject("owner"));

            // 插入 answer 数据
            insertAnswer(item, owner);
        }
    }

    @Test
    public void insertCommentsFromJson() throws IOException {
        // 读取 JSON 文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(COMMENT_JSON);
        if (inputStream == null) {
            throw new IOException("Unable to find the JSON file at " + QUESTION_JSON);
        }

        // 读取文件内容
        String jsonContent = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();
        inputStream.close();

        // 解析 JSON 数据
        JSONObject jsonObject = JSONObject.parseObject(jsonContent);
        JSONArray items = jsonObject.getJSONArray("items");

        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            // 插入 user 数据
            User owner = insertUser(item.getJSONObject("owner"));

            User replyToUser = insertUser(item.getJSONObject("reply_to_user"));

            // 插入 comment 数据
            insertComment(item, owner, replyToUser);
        }
    }

    /**
     * 插入评论数据
     *
     * @param item        评论 JSON 对象
     * @param owner       评论者
     * @param replyToUser 被回复的用户
     */
    public void insertComment(JSONObject item, User owner, User replyToUser) {
        // 判断评论是否存在
        Long commentId = item.getLong("comment_id");
        Comment existingComment = commentMapper.selectById(commentId);
        if (existingComment != null) {
            return;
        }

        // 判断问题或回答是否存在
        Long postId = item.getLong("post_id");
        Question question = questionMapper.selectById(postId);

        Answer answer = answerMapper.selectById(postId);

        if (question == null && answer == null) {
            throw new RuntimeException("问题或回答不存在");
        }

        // 插入 answer 数据
        Comment comment = new Comment()
                .setCommentId(commentId)
                .setPostId(postId)
                .setOwnerUserId(owner == null ? null : owner.getUserId())
                .setReplyToUserId(replyToUser == null ? null : replyToUser.getUserId())
                .setEdited(item.getBoolean("edited"))
                .setScore(item.getInteger("score"))

                // 时间字段解析
                .setCreationDate(parseDate(item.getLong("creation_date")))

                .setContentLicense(item.getString("content_license"));

        int insert = commentMapper.insert(comment);
        if (insert != 1) {
            throw new RuntimeException("插入回答失败");
        }
    }

    /**
     * 插入评论数据
     *
     * @param item 评论 JSON 对象
     * @param user 评论者
     */
    public void insertAnswer(JSONObject item, User user) {
        // 判断问题是否存在
        Long questionId = item.getLong("question_id");
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }

        // 插入 answer 数据
        Answer answer = new Answer()
                .setAnswerId(item.getLong("answer_id"))
                .setQuestionId(questionId)
                .setOwnerUserId(user == null ? null : user.getUserId())
                .setIsAccepted(item.getBoolean("is_accepted"))
                .setScore(item.getInteger("score"))

                // 时间字段解析
                .setCreationDate(parseDate(item.getLong("creation_date")))
                .setLastActivityDate(parseDate(item.getLong("last_activity_date")))
                .setCommunityOwnedDate(parseDate(item.getLong("community_owned_date")))

                .setContentLicense(item.getString("content_license"));

        int insert = answerMapper.insert(answer);
        if (insert != 1) {
            throw new RuntimeException("插入回答失败");
        }
    }

    /**
     * 插入问题数据
     *
     * @param item 问题 JSON 对象
     * @return 插入的问题对象
     */
    private Question insertQuestion(JSONObject item, User user) {
        Question question = new Question()
                .setQuestionId(item.getLong("question_id"))
                .setOwnerUserId(user == null ? null : user.getUserId())
                .setTitle(item.getString("title"))
                .setLink(item.getString("link"))
                .setIsAnswered(item.getBoolean("is_answered"))
                .setViewCount(item.getInteger("view_count"))
                .setAcceptedAnswerId(item.getLong("accepted_answer_id"))
                .setAnswerCount(item.getInteger("answer_count"))
                .setScore(item.getInteger("score"))

                // 时间字段解析
                .setCreationDate(parseDate(item.getLong("creation_date")))
                .setLastActivityDate(parseDate(item.getLong("last_activity_date")))
                .setLastEditDate(parseDate(item.getLong("last_edit_date")))
                .setProtectedDate(parseDate(item.getLong("protected_date")))

                .setContentLicense(item.getString("content_license"));

        int insert = questionMapper.insert(question);
        if (insert != 1) {
            throw new RuntimeException("插入问题失败");
        }
        return question;
    }

    /**
     * 插入用户数据
     *
     * @param userItem 用户 JSON 对象
     * @return 插入的用户对象
     */
    private User insertUser(JSONObject userItem) {
        if (userItem == null) {
            return null;
        }

        String userType = userItem.getString("user_type");
        if ("does_not_exist".equals(userType)) {
            return null;  // 如果用户不存在，返回null
        }

        Long userId = userItem.getLong("user_id");
        User existingUser = userMapper.selectById(userId);
        if (existingUser != null) {
            return existingUser; // 如果用户已经存在，直接返回
        }

        User user = new User()
                .setUserId(userId)
                .setAccountId(userItem.getLong("account_id"))
                .setReputation(userItem.getInteger("reputation"))
                .setUserType(userItem.getString("user_type"))
                .setAcceptRate(userItem.getInteger("accept_rate"))
                .setProfileImage(userItem.getString("profile_image"))
                .setDisplayName(userItem.getString("display_name"))
                .setLink(userItem.getString("link"));

        try {
            int insert = userMapper.insert(user);
            if (insert != 1) {
                throw new RuntimeException("插入用户失败");
            }
        } catch (Exception e) {
            System.out.println(user);
            throw e;
        }
        return user;
    }

    /**
     * 插入标签数据并处理关联
     *
     * @param tagsArray  标签数组
     * @param questionId 问题ID
     */
    private void insertTags(JSONArray tagsArray, Long questionId) {
        for (int j = 0; j < tagsArray.size(); j++) {
            String tagName = tagsArray.getString(j);

            // 查找标签是否存在
            Tag tag = tagMapper.selectOne(
                    new LambdaQueryWrapper<Tag>()
                            .eq(Tag::getTagName, tagName)
            );
            if (tag == null) {
                // 如果标签不存在，插入新标签
                tag = new Tag().setTagName(tagName);
                int insert = tagMapper.insert(tag);
                if (insert != 1) {
                    throw new RuntimeException("插入标签失败");
                }
            }

            // 将问题与标签关联
            insertQuestionTag(questionId, tag.getTagId());
        }
    }

    /**
     * 插入问题与标签的关联关系
     *
     * @param questionId 问题ID
     * @param tagId      标签ID
     */
    private void insertQuestionTag(Long questionId, Long tagId) {
        // 查找是否已经存在关联关系
        if (questionTagMapper.exists(
                new LambdaQueryWrapper<QuestionTag>()
                        .eq(QuestionTag::getQuestionId, questionId)
                        .eq(QuestionTag::getTagId, tagId)
        )) {
            return;
        }

        // 插入关联数据
        int insert = questionTagMapper.insert(
                new QuestionTag()
                        .setQuestionId(questionId)
                        .setTagId(tagId)
        );
        if (insert != 1) {
            throw new RuntimeException("插入关联数据失败");
        }
    }

    /**
     * 将时间戳转换为 LocalDateTime
     *
     * @param timestamp 时间戳
     * @return LocalDateTime 对象
     */
    private LocalDateTime parseDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(timestamp);

        // 将Instant转换为LocalDateTime
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
