package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.*;
import cn.edu.sustech.stackoverflow.entity.dto.TopicByEngagementQueryDTO;
import cn.edu.sustech.stackoverflow.entity.vo.*;
import cn.edu.sustech.stackoverflow.mapper.*;
import cn.edu.sustech.stackoverflow.service.AnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 9:47
 */
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final QuestionMapper questionMapper;

    private final QuestionTagMapper questionTagMapper;

    private final TagMapper tagMapper;

    private final AnswerMapper answerMapper;

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    /**
     * 获取所有数据的时间范围
     *
     * @return 所有数据的时间范围
     */
    @Override
    public DateRangeVO getDateRange() {
        List<Question> questions = questionMapper.selectList(null);

        LocalDateTime start = null;
        LocalDateTime end = null;

        for (Question question : questions) {
            if (start == null || question.getCreationDate().isBefore(start)) {
                start = question.getCreationDate();
            }
            if (end == null || question.getCreationDate().isAfter(end)) {
                end = question.getCreationDate();
            }
        }

        return DateRangeVO.builder()
                .start(start)
                .end(end)
                .build();
    }

    /**
     * 获取特定bug
     *
     * @param bugName   bug名字
     * @return 特定bug信息
     */
    @Override
    public Object getBugByBugName(String bugName) {
        // 查询所有的 Question
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>()
        );

        // 查询所有的 Answer
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
        );

        // 查询所有的 Comment
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
        );

        // 定义统计结果的 Map
        Map<String, Long> errorCountMap = new HashMap<>();
        Map<String, Long> exceptionCountMap = new HashMap<>();

        // 统计问题中的 error 和 exception
        questions.forEach(question -> {
            countOccurrences(question.getBody(), errorCountMap, exceptionCountMap);
            countOccurrences(question.getTitle(), errorCountMap, exceptionCountMap);
        });

        // 统计回答中的 error 和 exception
        answers.forEach(answer -> countOccurrences(answer.getBody(), errorCountMap, exceptionCountMap));

        // 统计评论中的 error 和 exception
        comments.forEach(comment -> countOccurrences(comment.getBody(), errorCountMap, exceptionCountMap));

        // 合并错误和异常的计数
        Map<String, Long> mixedCountMap = new HashMap<>();
        errorCountMap.forEach((key, value) -> mixedCountMap.put(key, mixedCountMap.getOrDefault(key, 0L) + value));
        exceptionCountMap.forEach((key, value) -> mixedCountMap.put(key, mixedCountMap.getOrDefault(key, 0L) + value));

        // 计算总计数
        long totalMixedCount = mixedCountMap.values().stream().mapToLong(Long::longValue).sum();

        // 查找第一个匹配的 bug
        Long bugFrequency = mixedCountMap.get(bugName);
        if (bugFrequency != null) {
            return ThrowableVO.builder()
                    .name(bugName)
                    .count(bugFrequency)
                    .percentage(totalMixedCount > 0 ? bugFrequency * 100.0 / totalMixedCount : 0.0)
                    .build();
        } else {
            return "未找到名称: " + bugName;
        }
    }

    /**
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    @Override
    public Object getTopNErrorsAndExceptions(Integer n, LocalDateTime start, LocalDateTime end, Boolean mixed) {
        // 查询符合时间范围的所有问题
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>()
                        .ge(start != null, Question::getCreationDate, start)
                        .le(end != null, Question::getCreationDate, end)
        );

        // 查询符合时间范围的所有回答
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .ge(start != null, Answer::getCreationDate, start)
                        .le(end != null, Answer::getCreationDate, end)
        );

        // 查询符合时间范围的所有评论
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .ge(start != null, Comment::getCreationDate, start)
                        .le(end != null, Comment::getCreationDate, end)
        );

        // 定义统计结果的 Map
        Map<String, Long> errorCountMap = new HashMap<>();
        Map<String, Long> exceptionCountMap = new HashMap<>();

        // 统计问题中的 error 和 exception
        questions.forEach(question -> {
            countOccurrences(question.getBody(), errorCountMap, exceptionCountMap);
            countOccurrences(question.getTitle(), errorCountMap, exceptionCountMap);
        });

        // 统计回答中的 error 和 exception
        answers.forEach(answer -> countOccurrences(answer.getBody(), errorCountMap, exceptionCountMap));

        // 统计评论中的 error 和 exception
        comments.forEach(comment -> countOccurrences(comment.getBody(), errorCountMap, exceptionCountMap));

        // 计算总计数
        long totalErrorCount = errorCountMap.values().stream().mapToLong(Long::longValue).sum();
        long totalExceptionCount = exceptionCountMap.values().stream().mapToLong(Long::longValue).sum();

        if (mixed) {
            // 合并错误和异常的计数
            Map<String, Long> mixedCountMap = new HashMap<>();
            long totalMixedCount = totalErrorCount + totalExceptionCount;
            errorCountMap.forEach((key, value) -> mixedCountMap.put(key, mixedCountMap.getOrDefault(key, 0L) + value));
            exceptionCountMap.forEach((key, value) -> mixedCountMap.put(key, mixedCountMap.getOrDefault(key, 0L) + value));
            return mixedCountMap.entrySet().stream()
                    .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                    .limit(n)
                    .map(entry -> ThrowableVO.builder()
                            .name(entry.getKey())
                            .count(entry.getValue())
                            .percentage(totalMixedCount > 0 ? entry.getValue() * 100.0 / totalMixedCount : 0.0)
                            .build())
                    .collect(Collectors.toList());
        }

        // 构建错误和异常的列表
        List<ThrowableVO> errors = errorCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(entry -> ErrorVO.builder()
                        .name(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalErrorCount > 0 ? entry.getValue() * 100.0 / totalErrorCount : 0.0)
                        .build())
                .collect(Collectors.toList());

        List<ThrowableVO> exceptions = exceptionCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(entry -> ExceptionVO.builder()
                        .name(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalExceptionCount > 0 ? entry.getValue() * 100.0 / totalExceptionCount : 0.0)
                        .build())
                .collect(Collectors.toList());

        // 返回结果
        return ErrorAndExceptionVO.builder()
                .errors(errors)
                .exceptions(exceptions)
                .build();
    }

    /**
     * 获取指定时间段内用户声望高的用户参与度最高的n个话题
     *
     * @param topicByEngagementQueryDTO 查询参数
     * @return 用户声望高的用户参与度最高的n个话题
     */
    @Override
    public List<TopicByEngagementVO> getTopNTopicsByEngagementOfUserWithHigherReputation(TopicByEngagementQueryDTO topicByEngagementQueryDTO) {

        Long questionCount = questionMapper.selectCount(null);
        Long answerCount = answerMapper.selectCount(null);
        Long commentCount = commentMapper.selectCount(null);

        // 计算问题、回答、评论的权重
        double questionWeight;
        double answerWeight;
        double commentWeight;

        if (topicByEngagementQueryDTO.getQuestionWeight() == null || topicByEngagementQueryDTO.getAnswerWeight() == null || topicByEngagementQueryDTO.getCommentWeight() == null) {
            questionWeight = 1.0 / 3;
            answerWeight = 1.0 / 3;
            commentWeight = 1.0 / 3;
        } else {
            double totalWeight = topicByEngagementQueryDTO.getQuestionWeight() + topicByEngagementQueryDTO.getAnswerWeight() + topicByEngagementQueryDTO.getCommentWeight();
            questionWeight = topicByEngagementQueryDTO.getQuestionWeight() / totalWeight;
            answerWeight = topicByEngagementQueryDTO.getAnswerWeight() / totalWeight;
            commentWeight = topicByEngagementQueryDTO.getCommentWeight() / totalWeight;
        }

        double commentScoreUnit = 1.0;
        double answerScoreUnit = commentScoreUnit * commentCount / answerCount;
        double questionScoreUnit = answerScoreUnit * answerCount / questionCount;

        double commentScore = commentScoreUnit * commentWeight;
        double answerScore = answerScoreUnit * answerWeight;
        double questionScore = questionScoreUnit * questionWeight;

        List<User> users = userMapper.selectList(null);
        users.sort((a, b) -> Long.compare(b.getReputation(), a.getReputation()));

        users = users.subList(0, (int) (users.size() * (topicByEngagementQueryDTO.getPercentage() == null ? 1 : topicByEngagementQueryDTO.getPercentage())));

        HashSet<Long> userIds = users.stream().map(User::getUserId).collect(Collectors.toCollection(HashSet::new));

        // 查询符合条件的所有问题
        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>()
                        .ge(topicByEngagementQueryDTO.getStart() != null, Question::getCreationDate, topicByEngagementQueryDTO.getStart())
                        .le(topicByEngagementQueryDTO.getEnd() != null, Question::getCreationDate, topicByEngagementQueryDTO.getEnd())
        );

        Set<Long> questionIds = questions.stream().map(Question::getQuestionId).collect(Collectors.toSet());
        if (questionIds.isEmpty()) {
            return List.of();
        }

        // 查询符合条件的所有回答
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .in(Answer::getQuestionId, questionIds)
                        .ge(topicByEngagementQueryDTO.getStart() != null, Answer::getCreationDate, topicByEngagementQueryDTO.getStart())
                        .le(topicByEngagementQueryDTO.getEnd() != null, Answer::getCreationDate, topicByEngagementQueryDTO.getEnd())
        );

        List<Long> answerIds = answers.stream().map(Answer::getAnswerId).toList();
        // answerId -> questionId 的映射map
        Map<Long, Long> answerToQuestion = answers.stream().collect(Collectors.toMap(Answer::getAnswerId, Answer::getQuestionId));

        // 查询符合条件的所有评论
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .in(Comment::getPostId, questionIds)
                        .or()
                        .in(Comment::getPostId, answerIds)
                        .ge(topicByEngagementQueryDTO.getStart() != null, Comment::getCreationDate, topicByEngagementQueryDTO.getStart())
                        .le(topicByEngagementQueryDTO.getEnd() != null, Comment::getCreationDate, topicByEngagementQueryDTO.getEnd())
        );

        // 初始化一个hashset记录每个问题id和对应的高声誉用户互动数据
        Map<Long, Map<String, Object>> questionToData =
                questions.stream().collect(Collectors.toMap(Question::getQuestionId, question -> Map.of(
                        "score", 0.0,
                        "questionScore", 0.0,
                        "answerScore", 0.0,
                        "commentScore", 0.0,
                        "questionCount", 0,
                        "answerCount", 0,
                        "commentCount", 0
                )));

        // 高声誉用户的问题、回答、评论互动数据
        questions.stream()
                .filter(question -> userIds.contains(question.getOwnerUserId()))
                .forEach(question -> {
                    Map<String, Object> data = new HashMap<>(questionToData.get(question.getQuestionId()));
                    data.put("questionScore", (double) data.get("questionScore") + questionScore);
                    data.put("questionCount", (int) data.get("questionCount") + 1);
                    questionToData.put(question.getQuestionId(), data);
                });

        answers.stream()
                .filter(answer -> userIds.contains(answer.getOwnerUserId()))
                .forEach(answer -> {
                    Map<String, Object> data = new HashMap<>(questionToData.get(answer.getQuestionId()));
                    data.put("answerScore", (double) data.get("answerScore") + answerScore);
                    data.put("answerCount", (int) data.get("answerCount") + 1);
                    questionToData.put(answer.getQuestionId(), data);
                });

        comments.stream()
                .filter(comment -> userIds.contains(comment.getOwnerUserId()))
                .forEach(comment -> {
                    Long questionId;

                    if (questionToData.containsKey(comment.getPostId())) {
                        // 如果是问题的评论
                        questionId = comment.getPostId();
                    } else {
                        // 如果是回答的评论
                        questionId = answerToQuestion.get(comment.getPostId());
                    }
                    Map<String, Object> data = new HashMap<>(questionToData.get(questionId));
                    data.put("commentScore", (double) data.get("commentScore") + commentScore);
                    data.put("commentCount", (int) data.get("commentCount") + 1);
                    questionToData.put(questionId, data);
                });

        // 相加得到总分
        questionToData.keySet().forEach(questionId -> {
            Map<String, Object> data = new HashMap<>(questionToData.get(questionId));
            data.put("score", (double) data.get("questionScore") + (double) data.get("answerScore") + (double) data.get("commentScore"));
            questionToData.put(questionId, data);
        });

        List<QuestionTag> questionTags = questionTagMapper.selectList(
                new LambdaQueryWrapper<QuestionTag>()
                        .in(QuestionTag::getQuestionId, questionIds)
        );

        // tagId的set
        HashSet<Long> tagIds = questionTags.stream().map(QuestionTag::getTagId).collect(Collectors.toCollection(HashSet::new));
        List<Tag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .in(Tag::getTagId, tagIds)
        );
        // tagId -> Tag 的映射map
        Map<Long, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getTagId, tag -> tag));

        // questionId -> List<Tag> 的映射map
        Map<Long, List<Tag>> questionToTags = questionTags.stream()
                .collect(Collectors.groupingBy(QuestionTag::getQuestionId,
                        Collectors.mapping(questionTag -> tagMap.get(questionTag.getTagId()), Collectors.toList())));

        // tagId -> TopicByEngagementVO 的映射map
        HashMap<Long, TopicByEngagementVO> tagToTopic = new HashMap<>();

        // questionToData -> TopicByEngagementVO
        questionToData.forEach((questionId, data) -> {
            List<Tag> questionTagsList = questionToTags.get(questionId);
            // 有的问题没有标签
            if (questionTagsList != null) {
                questionTagsList.forEach(tag -> {
                    TopicByEngagementVO topic = tagToTopic.get(tag.getTagId());
                    if (topic == null) {
                        topic = TopicByEngagementVO.builder()
                                .tagId(tag.getTagId())
                                .tagName(tag.getTagName())
                                .score(0.0)
                                .questionScore(0.0)
                                .answerScore(0.0)
                                .commentScore(0.0)
                                .questionCount(0)
                                .answerCount(0)
                                .commentCount(0)
                                .build();
                        tagToTopic.put(tag.getTagId(), topic);
                    }
                    topic.setScore(topic.getScore() + (double) data.get("score"));
                    topic.setQuestionScore(topic.getQuestionScore() + (double) data.get("questionScore"));
                    topic.setAnswerScore(topic.getAnswerScore() + (double) data.get("answerScore"));
                    topic.setCommentScore(topic.getCommentScore() + (double) data.get("commentScore"));
                    topic.setQuestionCount(topic.getQuestionCount() + (int) data.get("questionCount"));
                    topic.setAnswerCount(topic.getAnswerCount() + (int) data.get("answerCount"));
                    topic.setCommentCount(topic.getCommentCount() + (int) data.get("commentCount"));
                });
            }
        });


        return tagToTopic.values().stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(topicByEngagementQueryDTO.getN())
                .collect(Collectors.toList());
    }

    /**
     * 获取指定时间段内最先发布的回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 最先发布的回答信息及创建时间信息
     */
    @Override
    public List<AnswerWithCreateDateVO> getFirstAnswersWithCreateDate(LocalDateTime start, LocalDateTime end) {
        return getAnswersByCondition(true, false, start, end);
    }

    /**
     * 获取指定时间段内被接受的回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 被接受的回答信息及创建时间信息
     */
    @Override
    public List<AnswerWithCreateDateVO> getAcceptedAnswersWithCreateDate(LocalDateTime start, LocalDateTime end) {
        return getAnswersByCondition(false, true, start, end);
    }

    /**
     * 获取指定时间段内回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及创建时间信息
     */
    @Override
    public List<AnswerWithCreateDateVO> getAnswersWithCreateDate(LocalDateTime start, LocalDateTime end) {
        return getAnswersByCondition(false, false, start, end);
    }

    public List<AnswerWithCreateDateVO> getAnswersByCondition(boolean first, boolean accepted, LocalDateTime start, LocalDateTime end) {
        // 查询符合时间范围的所有回答
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .ge(start != null, Answer::getCreationDate, start)
                        .le(end != null, Answer::getCreationDate, end)
        );

        Set<Long> questionIds = answers.stream().map(Answer::getQuestionId).collect(Collectors.toSet());

        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>()
                        .in(Question::getQuestionId, questionIds)
        );

        // questionId -> Question 的映射map
        Map<Long, Question> questionMap = questions.stream().collect(Collectors.toMap(Question::getQuestionId, question -> question));

        Stream<List<AnswerWithCreateDateVO>> stream = answers.stream()
                .filter(answer -> questionMap.containsKey(answer.getQuestionId()))
                .map(answer -> AnswerWithCreateDateVO.builder()
                        .answerId(answer.getAnswerId())
                        .isAccepted(answer.getIsAccepted())
                        .upVoteCount(answer.getUpVoteCount())
                        .downVoteCount(answer.getDownVoteCount())
                        .answerCreateDate(answer.getCreationDate())
                        .duration(Duration.between(questionMap.get(answer.getQuestionId()).getCreationDate(), answer.getCreationDate()).toMillis())
                        .questionId(answer.getQuestionId())
                        .questionCreateDate(questionMap.get(answer.getQuestionId()).getCreationDate())
                        .build())
                // 按照questionId分组，每组按answerCreateDate-questionCreationDate升序排序;
                .collect(Collectors.groupingBy(
                        AnswerWithCreateDateVO::getQuestionId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    List<AnswerWithCreateDateVO> sortedList = list.stream()
                                            .sorted(Comparator.comparing(AnswerWithCreateDateVO::getDuration))
                                            .toList();

                                    // 给分组后的的第一个元素设置boolean first以及给每个元素根据index/size设置percentage
                                    int size = sortedList.size();
                                    for (int i = 0; i < size; i++) {
                                        sortedList.get(i).setFirst(i == 0);
                                    }
                                    return sortedList;
                                }
                        )
                )).values().stream();

        if (first) {
            return stream.map(List::getFirst).toList();
        }
        if (accepted) {
            return stream.flatMap(List::stream).filter(AnswerWithCreateDateVO::getIsAccepted).toList();
        }
        return stream.flatMap(List::stream).toList();
    }

    /**
     * 获取指定时间段内回答信息及回答用户声望
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及回答用户声望
     */
    @Override
    public List<AnswerWithUserReputationVO> getAnswersWithUserReputation(LocalDateTime start, LocalDateTime end) {
        // 查询符合时间范围的所有回答
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .ge(start != null, Answer::getCreationDate, start)
                        .le(end != null, Answer::getCreationDate, end)
        );

//        // 最多只能返回maxCount条数据, 如果超过maxCount条则随机返回maxCount条
//        int maxCount = 2000;
//        if (answers.size() > maxCount) {
//            Collections.shuffle(answers);
//            answers = answers.subList(0, maxCount);
//        }

        // 所有用户
        List<User> users = userMapper.selectList(null);

        // userId -> User 的映射map
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, user -> user));

        return answers.stream()
                .filter(answer -> answer.getOwnerUserId() != null)
                .map(answer -> AnswerWithUserReputationVO.builder()
                        .answerId(answer.getAnswerId())
                        .upVoteCount(answer.getUpVoteCount())
                        .downVoteCount(answer.getDownVoteCount())
                        .isAccepted(answer.getIsAccepted())
                        .reputation(userMap.get(answer.getOwnerUserId()).getReputation())
                        .build())
                .toList();
    }

    /**
     * 获取指定时间段内回答信息及回答长度
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及回答长度
     */
    @Override
    public List<AnswerWithLengthVO> getAnswersWithLength(LocalDateTime start, LocalDateTime end) {
        // 查询符合时间范围的所有回答
        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .ge(start != null, Answer::getCreationDate, start)
                        .le(end != null, Answer::getCreationDate, end)
        );

        return answers.stream()
                .map(answer -> AnswerWithLengthVO.builder()
                        .answerId(answer.getAnswerId())
                        .upVoteCount(answer.getUpVoteCount())
                        .downVoteCount(answer.getDownVoteCount())
                        .isAccepted(answer.getIsAccepted())
                        .length(answer.getBody().length())
                        .build())
                .toList();
    }

    /**
     * 获取数据概览
     *
     * @return 数据概览
     */
    @Override
    public OverviewVO getOverview() {
        return OverviewVO.builder()
                .questionCount(questionMapper.selectCount(null))
                .answerCount(answerMapper.selectCount(null))
                .commentCount(commentMapper.selectCount(null))
                .userCount(userMapper.selectCount(null))
                .build();
    }

    /**
     * 获取question、answer、comment每个月新产生的数量
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return question、answer、comment每个月新产生的数量
     */
    @Override
    public List<CountInSingleMonthVO> getCountInSingleMonth(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            DateRangeVO dateRange = getDateRange();
            if (start == null) {
                start = dateRange.getStart();
            }
            if (end == null) {
                end = dateRange.getEnd();
            }
        }

        List<Question> questions = questionMapper.selectList(
                new LambdaQueryWrapper<Question>()
                        .ge(Question::getCreationDate, start)
                        .lt(Question::getCreationDate, end)
        );

        List<Answer> answers = answerMapper.selectList(
                new LambdaQueryWrapper<Answer>()
                        .ge(Answer::getCreationDate, start)
                        .lt(Answer::getCreationDate, end)
        );

        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .ge(Comment::getCreationDate, start)
                        .lt(Comment::getCreationDate, end)
        );

        // 存储每个数据类型（问题、回答、评论）的统计结果
        List<CountInSingleMonthVO> result = new ArrayList<>();

        // 将数据按月分组并统计
        Map<String, Integer> questionMonthlyCounts = groupByMonthAndCount(questions, start, end);
        Map<String, Integer> answerMonthlyCounts = groupByMonthAndCount(answers, start, end);
        Map<String, Integer> commentMonthlyCounts = groupByMonthAndCount(comments, start, end);

        Set<String> allMonths = new HashSet<>();
        allMonths.addAll(questionMonthlyCounts.keySet());
        allMonths.addAll(answerMonthlyCounts.keySet());
        allMonths.addAll(commentMonthlyCounts.keySet());

        for (String month : allMonths) {
            CountInSingleMonthVO vo = new CountInSingleMonthVO();
            vo.setTime(month);
            vo.setQuestionCount(questionMonthlyCounts.getOrDefault(month, 0));
            vo.setAnswerCount(answerMonthlyCounts.getOrDefault(month, 0));
            vo.setCommentCount(commentMonthlyCounts.getOrDefault(month, 0));
            result.add(vo);
        }

        result.sort(Comparator.comparing(CountInSingleMonthVO::getTime));
        return result;
    }

    private <T> Map<String, Integer> groupByMonthAndCount(List<T> data, LocalDateTime start, LocalDateTime end) {
        Map<String, Integer> monthlyCounts = new HashMap<>();

        for (T item : data) {
            LocalDateTime creationDate = null;
            if (item instanceof Question) {
                creationDate = ((Question) item).getCreationDate();
            } else if (item instanceof Answer) {
                creationDate = ((Answer) item).getCreationDate();
            } else if (item instanceof Comment) {
                creationDate = ((Comment) item).getCreationDate();
            }

            if (creationDate != null && (creationDate.isAfter(start) || creationDate.isEqual(start)) && creationDate.isBefore(end)) {
                String month = creationDate.toLocalDate().toString().substring(0, 7);
                monthlyCounts.put(month, monthlyCounts.getOrDefault(month, 0) + 1);
            }
        }

        return monthlyCounts;
    }

    /**
     * 统计 body 中 error 和 exception 的出现次数，并更新到对应的 Map 中。
     *
     * @param body              需要统计的文本
     * @param errorCountMap     错误统计 Map
     * @param exceptionCountMap 异常统计 Map
     */
    private void countOccurrences(String body, Map<String, Long> errorCountMap, Map<String, Long> exceptionCountMap) {

        if (body == null || body.isEmpty()) {
            return;
        }

        Pattern errorPattern = Pattern.compile("(?<!\\w)(\\w+Error)(?!\\w)"); // 匹配完整单词，忽略大小写
        Matcher errorMatcher = errorPattern.matcher(body);

        // 使用 set 去重，保证一个单词只统计一次(在一个body中)
        HashSet<String> set = new HashSet<>();
        while (errorMatcher.find()) {
            String errorWord = errorMatcher.group(1); // 获取完整的单词
            if (set.contains(errorWord)) {
                continue;
            }
            set.add(errorWord);
            errorCountMap.put(errorWord, errorCountMap.getOrDefault(errorWord, 0L) + 1);
        }

        Pattern exceptionPattern = Pattern.compile("(?<!\\w)(\\w+Exception)(?!\\w)");
        Matcher exceptionMatcher = exceptionPattern.matcher(body);

        set = new HashSet<>();
        while (exceptionMatcher.find()) {
            String exceptionWord = exceptionMatcher.group(1);
            if (set.contains(exceptionWord)) {
                continue;
            }
            set.add(exceptionWord);
            exceptionCountMap.put(exceptionWord, exceptionCountMap.getOrDefault(exceptionWord, 0L) + 1);
        }
    }
}
