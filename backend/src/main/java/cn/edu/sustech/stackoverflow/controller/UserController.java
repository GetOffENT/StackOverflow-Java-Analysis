package cn.edu.sustech.stackoverflow.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/backend/user")
@Slf4j
@Tag(name = "用户相关接口", description = "用户相关接口")
@RequiredArgsConstructor
public class UserController {

}
