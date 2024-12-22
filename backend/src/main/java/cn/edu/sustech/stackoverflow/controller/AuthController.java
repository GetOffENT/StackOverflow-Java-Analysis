package cn.edu.sustech.stackoverflow.controller;

import cn.edu.sustech.stackoverflow.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 5:50
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "登录相关接口", description = "登录相关接口")
@Hidden
@RequiredArgsConstructor
public class AuthController {

    /**
     * 登录
     * @return 登录token
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> userLoginDTO) {
        log.info("登录, userLoginDTO: {}", userLoginDTO);
        return Result.success(Map.of("user", Map.of("id", 1)));
    }

    /**
     * 获取登录用户信息
     * @return 用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取登录用户信息")
    public Result<Map<String, Object>> info(String token) {
        log.info("获取登录用户信息, token: {}", token);
        Map<String, Object> map = Map.of(
                "roles", List.of("admin"),
                "introduction", "I am a super administrator",
                "avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
                "name", "Super Admin"
        );
        return Result.success(map);
    }

}
