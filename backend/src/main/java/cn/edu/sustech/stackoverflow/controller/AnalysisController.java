package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.entity.vo.TagVO;
import cn.edu.sustech.stackoverflow.result.Result;
import cn.edu.sustech.stackoverflow.service.TagService;
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

    private final TagService tagService;

    /**
     * 获取指定时间段内的前n个热门标签
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个热门标签
     */
    @GetMapping("/top")
    @Operation(summary = "获取前n个热门标签")
    public Result<List<TagVO>> getTopNTags(
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
    @GetMapping("/race")
    @Operation(summary = "获取指定时间段内race chart数据(一年一次)")
    public Result<List<TagVO>> getRaceChartData(
            @RequestParam Integer n,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end
    ) {
        log.info("获取指定时间段内race chart数据(一年一次) n:{}", n);
        return Result.success(tagService.getRaceChartData(n, start, end));
    }

}
