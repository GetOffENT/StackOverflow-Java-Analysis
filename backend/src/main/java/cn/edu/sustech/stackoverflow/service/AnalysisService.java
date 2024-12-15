package cn.edu.sustech.stackoverflow.service;

import cn.edu.sustech.stackoverflow.entity.vo.ErrorAndExceptionVO;

import java.time.LocalDateTime;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 9:47
 */
public interface AnalysisService {

    /**
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    ErrorAndExceptionVO getTopNErrorsAndExceptions(Integer n, LocalDateTime start, LocalDateTime end);
}
