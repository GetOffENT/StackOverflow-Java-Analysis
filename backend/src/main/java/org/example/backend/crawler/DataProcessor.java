package org.example.backend.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.entity.*;
import org.example.backend.mapper.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-13 0:26
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataProcessor {

    private final QuestionMapper questionMapper;

    private final UserMapper userMapper;

    private final TagMapper tagMapper;

    private final QuestionTagMapper questionTagMapper;

    private final AnswerMapper answerMapper;

    private final CommentMapper commentMapper;

    /**
     * 初始化数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public void initDatabase() {
        log.info("初始化数据库...");
        log.info("清空question表...");
        questionMapper.delete(null);

        log.info("清空user表...");
        userMapper.delete(null);

        log.info("清空tag表...");
        tagMapper.delete(null);

        log.info("清空question_tag表...");
        questionTagMapper.delete(null);

        log.info("清空answer表...");
        answerMapper.delete(null);

        log.info("清空comment表...");
        commentMapper.delete(null);
        log.info("数据库初始化完成");
    }

    /**
     * 处理问题信息数据
     *
     * @param items 问题信息数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleQuestions(JSONArray items) {
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            Long questionId = item.getLong("question_id");
            Question selectById = questionMapper.selectById(questionId);
            if (selectById != null) {
                log.info("问题已存在, 暂时跳过, questionId: {}", questionId);
                continue;
            }

            // 插入 user 数据
            User owner = insertUser(item.getJSONObject("owner"));

            // 插入 question 数据
            insertQuestion(item, owner);

            // 插入 tag 数据并处理关联
            insertTags(item.getJSONArray("tags"), questionId);

            // 插入 answers 数据
            JSONArray answers = item.getJSONArray("answers");
            if (answers != null) {
                insertAnswers(answers);
            }

            // 插入 comments 数据
            JSONArray comments = item.getJSONArray("comments");
            if (comments != null) {
                insertComments(comments);
            }
        }

    }

    /**
     * 插入评论数据
     *
     * @param comments 评论数组
     */
    private void insertComments(JSONArray comments) {
        for (int i = 0; i < comments.size(); i++) {
            JSONObject item = comments.getJSONObject(i);

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
        // 插入 answer 数据
        Comment comment = new Comment()
                .setCommentId(item.getLong("comment_id"))
                .setPostId(item.getLong("post_id"))
                .setOwnerUserId(owner == null ? null : owner.getUserId())
                .setReplyToUserId(replyToUser == null ? null : replyToUser.getUserId())
                .setBody(item.getString("body"))
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
     * 插入回答数据
     *
     * @param answers 回答数组
     */
    private void insertAnswers(JSONArray answers) {
        for (int i = 0; i < answers.size(); i++) {
            JSONObject item = answers.getJSONObject(i);

            // 插入 user 数据
            User owner = insertUser(item.getJSONObject("owner"));

            // 插入 answer 数据
            insertAnswer(item, owner);

            // 插入 comments 数据
            JSONArray comments = item.getJSONArray("comments");
            if (comments != null) {
                insertComments(comments);
            }
        }
    }

    /**
     * 插入回答数据
     *
     * @param item  回答 JSON 对象
     * @param owner 回答者
     */
    private void insertAnswer(JSONObject item, User owner) {
        // 插入 answer 数据
        Answer answer = new Answer()
                .setAnswerId(item.getLong("answer_id"))
                .setQuestionId(item.getLong("question_id"))
                .setOwnerUserId(owner == null ? null : owner.getUserId())
                .setBody(item.getString("body"))
                .setIsAccepted(item.getBoolean("is_accepted"))
                .setUpVoteCount(item.getInteger("up_vote_count"))
                .setDownVoteCount(item.getInteger("down_vote_count"))
                .setScore(item.getInteger("score"))
                .setCommentCount(item.getInteger("comment_count"))

                // 时间字段解析
                .setCreationDate(parseDate(item.getLong("creation_date")))
                .setLastActivityDate(parseDate(item.getLong("last_activity_date")))
                .setLastEditDate(parseDate(item.getLong("last_edit_date")))
                .setCommunityOwnedDate(parseDate(item.getLong("community_owned_date")))

                .setContentLicense(item.getString("content_license"));

        int insert = answerMapper.insert(answer);
        if (insert != 1) {
            throw new RuntimeException("插入回答失败");
        }
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

        int insert = userMapper.insert(user);
        if (insert != 1) {
            throw new RuntimeException("插入用户失败");
        }
        return user;
    }

    /**
     * 插入问题数据
     *
     * @param item 问题 JSON 对象
     */
    private void insertQuestion(JSONObject item, User user) {
        Question question = new Question()
                .setQuestionId(item.getLong("question_id"))
                .setOwnerUserId(user == null ? null : user.getUserId())
                .setTitle(item.getString("title"))
                .setBody(item.getString("body"))
                .setLink(item.getString("link"))
                .setIsAnswered(item.getBoolean("is_answered"))
                .setAnswerCount(item.getInteger("answer_count"))
                .setAcceptedAnswerId(item.getLong("accepted_answer_id"))
                .setCommentCount(item.getInteger("comment_count"))
                .setViewCount(item.getInteger("view_count"))
                .setFavoriteCount(item.getInteger("favorite_count"))
                .setUpVoteCount(item.getInteger("up_vote_count"))
                .setDownVoteCount(item.getInteger("down_vote_count"))
                .setScore(item.getInteger("score"))

                // 时间字段解析
                .setCreationDate(parseDate(item.getLong("creation_date")))
                .setLastActivityDate(parseDate(item.getLong("last_activity_date")))
                .setLastEditDate(parseDate(item.getLong("last_edit_date")))
                .setProtectedDate(parseDate(item.getLong("protected_date")))
                .setCommunityOwnedDate(parseDate(item.getLong("community_owned_date")))

                .setContentLicense(item.getString("content_license"));

        int insert = questionMapper.insert(question);
        if (insert != 1) {
            throw new RuntimeException("插入问题失败");
        }
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
