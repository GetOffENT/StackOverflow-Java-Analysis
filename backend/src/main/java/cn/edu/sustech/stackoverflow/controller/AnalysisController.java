package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.entity.vo.ErrorAndExceptionVO;
import cn.edu.sustech.stackoverflow.result.Result;
import cn.edu.sustech.stackoverflow.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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

}
