package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.entity.dto.TopicByEngagementQueryDTO;
import cn.edu.sustech.stackoverflow.entity.vo.*;
import cn.edu.sustech.stackoverflow.result.Result;
import cn.edu.sustech.stackoverflow.service.AnalysisService;
import cn.edu.sustech.stackoverflow.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 数据分析 前端控制器
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
@Tag(name = "数据分析相关接口", description = "数据分析相关接口")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    private final TagService tagService;

    /**
     * 根据bug名获取对应bug数据
     *
     * @param bugName 标签名
     * @return 对应标签数据
     */
    @GetMapping("/error-and-exception")
    @Operation(summary = "根据bug名获取对应bug数据")
    public Result<Object> getBugByBugName(@RequestParam String bugName) {
        log.info("根据bug名获取对应bug数据 bugName:{}", bugName);
        return Result.success(analysisService.getBugByBugName(bugName));
    }
    /**
     * 获取数据概览
     *
     * @return 数据概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取数据概览")
    public Result<OverviewVO> getOverview() {
        log.info("获取数据概览");
        return Result.success(analysisService.getOverview());
    }

    /**
     * 获取question、answer、comment每个月新产生的数量
     *
     * @return question、answer、comment每个月新产生的数量
     */
    @GetMapping("/overview/2")
    @Operation(summary = "获取question、answer、comment、user每个月新产生的数量")
    public Result<List<CountInSingleMonthVO>> getCountInSingleMonth(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取question、answer、comment、user每个月新产生的数量");
        return Result.success(analysisService.getCountInSingleMonth(start, end));
    }

    /**
     * 获取所有数据的时间范围
     *
     * @return 所有数据的时间范围
     */
    @GetMapping("/date-range")
    @Operation(summary = "获取所有数据的时间范围")
    public Result<DateRangeVO> getDateRange() {
        log.info("获取所有数据的时间范围");
        return Result.success(analysisService.getDateRange());
    }

    /**
     * 根据标签名获取对应标签数据
     *
     * @param tagName 标签名
     * @return 对应标签数据
     */
    @GetMapping("/topic")
    @Operation(summary = "根据标签名获取对应标签数据")
    public Result<TopicVO> getTopicByTagName(@RequestParam String tagName) {
        log.info("根据标签名获取对应标签数据 tagName:{}", tagName);
        return Result.success(tagService.getTopicByTagName(tagName));
    }

    /**
     * 获取指定时间段内的前n个热门标签
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个热门标签
     */
    @GetMapping("/topic/top")
    @Operation(summary = "获取前n个热门标签")
    public Result<List<TopicVO>> getTopNTags(
            @RequestParam Integer n,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取前n个热门标签 n:{}", n);
        return Result.success(tagService.getTopNTags(n, start, end));
    }

    /**
     * 获取指定时间段内race chart数据(一年一次)
     *
     * @param n     前n名
     * @param start 开始时间
     * @param end   结束时间
     * @return race chart数据
     */
    @GetMapping("/topic/race")
    @Operation(summary = "获取指定时间段内race chart数据(一年一次)")
    public Result<List<TopicVO>> getRaceChartData(
            @RequestParam Integer n,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内race chart数据(一年一次) n:{}", n);
        return Result.success(tagService.getRaceChartData(n, start, end));
    }


    /**
     * 获取指定时间段内用户声望高的用户参与度最高的n个话题
     *
     * @param topicByEngagementQueryDTO 查询参数
     * @return 用户声望高的用户参与度最高的n个话题
     */
    @GetMapping("/topic/engagement/top")
    @Operation(summary = "获取指定时间段内用户声望高的用户参与度最高的n个话题")
    public Result<List<TopicByEngagementVO>> getTopNTopicsByEngagementOfUserWithHigherReputation(
            @ParameterObject TopicByEngagementQueryDTO topicByEngagementQueryDTO
    ) {
        log.info("获取指定时间段内用户声望高的用户参与度最高的n个话题 查询参数:{}", topicByEngagementQueryDTO);
        return Result.success(analysisService.getTopNTopicsByEngagementOfUserWithHigherReputation(topicByEngagementQueryDTO));
    }

    /**
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    @GetMapping("/error-and-exception/top")
    @Operation(summary = "获取前n个被高频讨论的错误和异常", description = "获取前n个被高频讨论的错误和异常")
    public Result<Object> getTopNErrorsAndExceptions(
            @RequestParam Integer n,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
            @RequestParam(required = false, defaultValue = "false") Boolean mixed
    ) {
        log.info("获取前n个被讨论的高频错误和异常 n:{}", n);
        return Result.success(analysisService.getTopNErrorsAndExceptions(n, start, end, mixed));
    }

    /**
     * 获取指定时间段内最先发布的回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 最先发布的回答信息及创建时间信息
     */
    @GetMapping("/answer-quality/create-date/first")
    @Operation(summary = "获取指定时间段内最先发布的回答信息及创建时间信息")
    public Result<List<AnswerWithCreateDateVO>> getFirstAnswersWithCreateDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内最先发布的回答信息及创建时间信息 start:{} end:{}", start, end);
        return Result.success(analysisService.getFirstAnswersWithCreateDate(start, end));
    }

    /**
     * 获取指定时间段内被接受的回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 被接受的回答信息及创建时间信息
     */
    @GetMapping("/answer-quality/create-date/accepted")
    @Operation(summary = "获取指定时间段内被接受的回答信息及创建时间信息")
    public Result<List<AnswerWithCreateDateVO>> getAcceptedAnswersWithCreateDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内被接受的回答信息及创建时间信息 start:{} end:{}", start, end);
        return Result.success(analysisService.getAcceptedAnswersWithCreateDate(start, end));
    }

    /**
     * 获取指定时间段内回答信息及创建时间信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及创建时间信息
     */
    @GetMapping("/answer-quality/create-date/all")
    @Operation(summary = "获取指定时间段内回答信息及创建时间信息")
    public Result<List<AnswerWithCreateDateVO>> getAnswersWithCreateDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内回答信息及创建时间信息 start:{} end:{}", start, end);
        return Result.success(analysisService.getAnswersWithCreateDate(start, end));
    }

    /**
     * 获取指定时间段内回答简略信息及回答用户声望
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答简略信息及回答用户声望列表
     */
    @GetMapping("/answer-quality/reputation")
    @Operation(summary = "获取指定时间段内回答简略信息及回答用户声望")
    public Result<List<AnswerWithUserReputationVO>> getAnswersWithUserReputation(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内回答简略信息及回答用户声望 start:{} end:{}", start, end);
        return Result.success(analysisService.getAnswersWithUserReputation(start, end));
    }

    /**
     * 获取指定时间段内回答简略信息及答案长度信息
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答简略信息及答案长度列表
     */
    @GetMapping("/answer-quality/length")
    @Operation(summary = "获取指定时间段内回答简略信息及答案长度信息")
    public Result<List<AnswerWithLengthVO>> getAnswersWithLength(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内回答简略信息及答案长度信息 start:{} end:{}", start, end);
        return Result.success(analysisService.getAnswersWithLength(start, end));
    }
}
