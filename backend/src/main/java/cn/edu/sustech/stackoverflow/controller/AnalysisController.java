package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.entity.dto.TopicByEngagementQueryDTO;
import cn.edu.sustech.stackoverflow.entity.vo.AnswerWithUserReputationVO;
import cn.edu.sustech.stackoverflow.entity.vo.ErrorAndExceptionVO;
import cn.edu.sustech.stackoverflow.entity.vo.TopicByEngagementVO;
import cn.edu.sustech.stackoverflow.entity.vo.TopicVO;
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
     * 根据标签名获取对应标签数据
     *
     * @param tagName 标签名
     * @return 对应标签数据
     */
    @GetMapping("/topic")
    @Operation(summary = "根据标签名获取对应标签数据")
    public Result<TopicVO> getTopicByTagName (@RequestParam String tagName) {
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
     * @param n 前n名
     * @param start 开始时间
     * @param end 结束时间
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
    public Result<ErrorAndExceptionVO> getTopNErrorsAndExceptions(
            @RequestParam Integer n,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取前n个被讨论的高频错误和异常 n:{}", n);
        return Result.success(analysisService.getTopNErrorsAndExceptions(n, start, end));
    }

    /**
     * 获取指定时间段内所有回答简略信息及回答用户声望
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 所有回答简略信息及回答用户声望
     */
    @GetMapping("/answer-quality/reputation")
    @Operation(summary = "获取指定时间段内所有回答简略信息及回答用户声望")
    public Result<List<AnswerWithUserReputationVO>> getAnswersWithUserReputation(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内所有回答简略信息及回答用户声望 start:{} end:{}", start, end);
        return Result.success(analysisService.getAnswersWithUserReputation(start, end));
    }
}
