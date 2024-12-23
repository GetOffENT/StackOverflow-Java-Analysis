package cn.edu.sustech.stackoverflow.aspect;

import cn.edu.sustech.stackoverflow.annotation.CacheableRedis;
import cn.edu.sustech.stackoverflow.result.Result;
import cn.edu.sustech.stackoverflow.util.RedisCacheUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-23 8:56
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisCacheUtil redisCacheUtil;

    @Around("@annotation(cacheableRedis)")
    public Object handleCache(ProceedingJoinPoint joinPoint, CacheableRedis cacheableRedis){
        // 获取方法签名和参数
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // 获取缓存key
        String cacheKey = methodName + ":" + JSONObject.toJSONString(args);

        // 查询 Redis 缓存
        Object cachedData = redisCacheUtil.get(cacheKey);
        if (cachedData != null) {
            return JSONObject.parseObject(cachedData.toString(), Result.class);
        }

        // 缓存不存在，执行方法，并将结果缓存
        try {
            Object result = joinPoint.proceed();
            redisCacheUtil.set(cacheKey, JSONObject.toJSONString(result), cacheableRedis.expire(), TimeUnit.MINUTES);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
