package cn.edu.sustech.stackoverflow.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评论信息表 前端控制器
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/backend/comment")
@Slf4j
@Api(tags = "评论相关接口")
@RequiredArgsConstructor
public class CommentController {

}
