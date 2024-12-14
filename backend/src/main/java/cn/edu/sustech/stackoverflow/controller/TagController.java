package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.entity.vo.TagVO;
import cn.edu.sustech.stackoverflow.result.Result;
import cn.edu.sustech.stackoverflow.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 标签信息表 前端控制器
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/backend/tag")
@Slf4j
@Tag(name = "标签相关接口", description = "标签相关接口")
@RequiredArgsConstructor
public class TagController {

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
}
