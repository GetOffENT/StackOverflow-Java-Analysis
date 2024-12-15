package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.*;
import cn.edu.sustech.stackoverflow.entity.vo.ErrorAndExceptionVO;
import cn.edu.sustech.stackoverflow.entity.vo.ErrorVO;
import cn.edu.sustech.stackoverflow.entity.vo.ExceptionVO;
import cn.edu.sustech.stackoverflow.mapper.*;
import cn.edu.sustech.stackoverflow.service.AnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final TagMapper tagMapper;

    private final QuestionMapper questionMapper;

    private final AnswerMapper answerMapper;

    private final CommentMapper commentMapper;

    /**
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    @Override
    public ErrorAndExceptionVO getTopNErrorsAndExceptions(Integer n, LocalDateTime start, LocalDateTime end) {
        // 查询所有标签
        List<Tag> tags = tagMapper.selectList(null);

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

        // 统计标签中的 error 和 exception
        tags.forEach(tag -> {
            String tagName = tag.getTagName().toLowerCase();
            if (tagName.contains("error")) {
                errorCountMap.put(tagName, errorCountMap.getOrDefault(tagName, 0L) + 1);
            }
            if (tagName.contains("exception")) {
                exceptionCountMap.put(tagName, exceptionCountMap.getOrDefault(tagName, 0L) + 1);
            }
        });

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

        // 构建错误和异常的列表
        List<ErrorVO> errors = errorCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(entry -> ErrorVO.builder()
                        .name(entry.getKey())
                        .count(entry.getValue())
                        .percentage(totalErrorCount > 0 ? entry.getValue() * 100.0 / totalErrorCount : 0.0)
                        .build())
                .collect(Collectors.toList());

        List<ExceptionVO> exceptions = exceptionCountMap.entrySet().stream()
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
        String lowerCaseBody = body.toLowerCase();

        // 使用正则表达式匹配以 'error' 结尾的完整单词
        Pattern errorPattern = Pattern.compile("\\b\\w*error\\b"); // 匹配以 'error' 结尾的完整单词
        Matcher errorMatcher = errorPattern.matcher(lowerCaseBody);
        while (errorMatcher.find()) {
            String errorWord = errorMatcher.group();
            errorCountMap.put(errorWord, errorCountMap.getOrDefault(errorWord, 0L) + 1);
        }

        // 使用正则表达式匹配以 'exception' 结尾的完整单词
        Pattern exceptionPattern = Pattern.compile("\\b\\w*exception\\b"); // 匹配以 'exception' 结尾的完整单词
        Matcher exceptionMatcher = exceptionPattern.matcher(lowerCaseBody);
        while (exceptionMatcher.find()) {
            String exceptionWord = exceptionMatcher.group();
            exceptionCountMap.put(exceptionWord, exceptionCountMap.getOrDefault(exceptionWord, 0L) + 1);
        }
    }
}
