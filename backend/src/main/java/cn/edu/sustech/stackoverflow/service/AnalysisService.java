package cn.edu.sustech.stackoverflow.service;

import cn.edu.sustech.stackoverflow.entity.dto.TopicByEngagementQueryDTO;
import cn.edu.sustech.stackoverflow.entity.vo.AnswerWithUserReputationVO;
import cn.edu.sustech.stackoverflow.entity.vo.ErrorAndExceptionVO;
import cn.edu.sustech.stackoverflow.entity.vo.TopicByEngagementVO;

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
     * 获取前n个被高频讨论的错误和异常
     *
     * @param n     前n个
     * @param start 开始时间
     * @param end   结束时间
     * @return 前n个被高频讨论的错误和异常
     */
    ErrorAndExceptionVO getTopNErrorsAndExceptions(Integer n, LocalDateTime start, LocalDateTime end);

    /**
     * 获取指定时间段内用户声望高的用户参与度最高的n个话题
     *
     * @param topicByEngagementQueryDTO 查询参数
     * @return 用户声望高的用户参与度最高的n个话题
     */
    List<TopicByEngagementVO> getTopNTopicsByEngagementOfUserWithHigherReputation(TopicByEngagementQueryDTO topicByEngagementQueryDTO);

    /**
     * 获取指定时间段内回答信息及回答用户声望
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 回答信息及回答用户声望
     */
    List<AnswerWithUserReputationVO> getAnswersWithUserReputation(LocalDateTime start, LocalDateTime end);
}
