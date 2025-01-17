package cn.edu.sustech.stackoverflow.service;

import cn.edu.sustech.stackoverflow.entity.dto.TopicByEngagementQueryDTO;
import cn.edu.sustech.stackoverflow.entity.vo.*;

import java.time.LocalDateTime;
import java.util.List;

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
     * 获取特定bug
     *
     * @param bugName bug名字
     * @return 特定bug信息
     */
    Object getBugByBugName(String bugName);

    /**
     * 获取所有数据的时间范围
     *
     * @return 所有数据的时间范围
     */
    DateRangeVO getDateRange();

    /**
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    Object getTopNErrorsAndExceptions(Integer n, LocalDateTime start, LocalDateTime end, Boolean mixed);

    /**
     * 获取指定时间段内用户声望高的用户参与度最高的n个话题
     *
     * @param topicByEngagementQueryDTO 查询参数
     * @return 用户声望高的用户参与度最高的n个话题
     */
    List<TopicByEngagementVO> getTopNTopicsByEngagementOfUserWithHigherReputation(TopicByEngagementQueryDTO topicByEngagementQueryDTO);

    /**
     * 获取指定时间段内回答信息及创建时间信息
     *
     * @param start    开始时间
     * @param end      结束时间
     * @param accepted 是否只获取被接受的回答, 默认为false
     * @param first    是否只获取最先发布的回答， 默认为false
     * @return 回答信息及创建时间信息
     */
    List<AnswerWithCreateDateVO> getAnswersWithCreateDate(LocalDateTime start, LocalDateTime end, Boolean accepted, Boolean first);

    /**
     * 获取指定时间段内回答信息及回答用户声望
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及回答用户声望
     */
    List<AnswerWithUserReputationVO> getAnswersWithUserReputation(LocalDateTime start, LocalDateTime end);

    /**
     * 获取指定时间段内回答信息及回答长度
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及回答长度
     */
    List<AnswerWithLengthVO> getAnswersWithLength(LocalDateTime start, LocalDateTime end);

    /**
     * 获取数据概览
     *
     * @return 数据概览
     */
    OverviewVO getOverview();

    /**
     * 获取question、answer、comment每个月新产生的数量
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return question、answer、comment每个月新产生的数量
     */
    List<CountInSingleMonthVO> getCountInSingleMonth(LocalDateTime start, LocalDateTime end);
}
