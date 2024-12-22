package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.Question;
import cn.edu.sustech.stackoverflow.entity.QuestionTag;
import cn.edu.sustech.stackoverflow.entity.Tag;
import cn.edu.sustech.stackoverflow.entity.vo.TopicVO;
import cn.edu.sustech.stackoverflow.mapper.QuestionMapper;
import cn.edu.sustech.stackoverflow.mapper.QuestionTagMapper;
import cn.edu.sustech.stackoverflow.service.TagService;
import cn.edu.sustech.stackoverflow.mapper.TagMapper;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签信息表 服务实现类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;

    private final QuestionTagMapper questionTagMapper;

    private final QuestionMapper questionMapper;


    /**
     * 根据标签名获取对应标签数据
     *
     * @param tagName 标签名
     * @return 对应标签数据
     */
    @Override
    public TopicVO getTopicByTagName(String tagName) {

        Long total = questionTagMapper.selectCount(null);
        Tag tag = tagMapper.selectOne(
                new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagName)
        );
        if (tag == null) {
            return null;
        }
        return TopicVO.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .count(tag.getCount())
                .percentage((double) tag.getCount() / total)
                .build();
    }

    /**
     * 获取指定时间段内的前n个热门标签
     *
     * @param n     前n个
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 前n个热门标签
     */
    @Override
    public List<TopicVO> getTopNTags(Integer n, LocalDateTime startTime, LocalDateTime endTime) {

        List<TopicVO> topicVOS;
        if (startTime == null && endTime == null) {
            List<Tag> tagList = tagMapper.selectList(null);

        // 按照count排序
        tagList.sort((o1, o2) -> o2.getCount() - o1.getCount());

        Integer count = tagList.stream().map(Tag::getCount).reduce(Integer::sum).orElse(0);

            topicVOS = BeanUtil.copyToList(tagList, TopicVO.class);

            topicVOS.forEach(topicVO -> topicVO.setPercentage((double) topicVO.getCount() / count));
        } else {
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();

            if (startTime != null) {
                queryWrapper.ge(Question::getCreationDate, startTime);
            }

            if (endTime != null) {
                queryWrapper.le(Question::getCreationDate, endTime);
            }

            List<Question> questions = questionMapper.selectList(queryWrapper);

            List<Long> questionIds = questions.stream().map(Question::getQuestionId).toList();
            if (questionIds.isEmpty()) {
                return List.of();
            }
            List<QuestionTag> questionTags = questionTagMapper.selectList(
                    new LambdaQueryWrapper<QuestionTag>()
                            .in(QuestionTag::getQuestionId, questionIds)
            );

            // 统计每个tag出现的次数
            Map<Long, Long> tagCount = questionTags.stream().collect(Collectors.groupingBy(QuestionTag::getTagId, Collectors.counting()));

            int totalCount = questionTags.size();

            // tagId, 并去重
            List<Long> tagIds = questionTags.stream().map(QuestionTag::getTagId).distinct().toList();
            if (tagIds.isEmpty()) {
                return List.of();
            }
            List<Tag> tags = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>()
                            .in(Tag::getTagId, tagIds)
            );

            topicVOS = tags.stream().map(tag -> {
                int count = tagCount.getOrDefault(tag.getTagId(), 0L).intValue();
                return TopicVO.builder()
                        .tagId(tag.getTagId())
                        .tagName(tag.getTagName())
                        .count(count)
                        .percentage((double) count / totalCount)
                        .build();
            }).sorted(Comparator.comparing(TopicVO::getCount).reversed()).toList();
        }

        if (topicVOS.size() > n) {
            return topicVOS.subList(0, n);
        } else {
            return topicVOS;
        }
    }

    /**
     * 获取指定时间段内race chart数据(一年一次)
     *
     * @param n     前n名
     * @param start 开始时间
     * @param end   结束时间
     * @return race chart数据
     */
    @Override
    public List<TopicVO> getRaceChartData(Integer n, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            List<Question> questions = questionMapper.selectList(null);

            LocalDateTime min = questions.getFirst().getCreationDate();
            LocalDateTime max = questions.getFirst().getCreationDate();
            for (Question question : questions) {
                if (question.getCreationDate().isBefore(min)) {
                    min = question.getCreationDate();
                }
                if (question.getCreationDate().isAfter(max)) {
                    max = question.getCreationDate();
                }
            }
            if (start == null) {
                start = min;
            }
            if (end == null) {
                end = max;
            }
        }

        // 只取start和end的年份
        start = LocalDateTime.of(start.getYear(), 1, 1, 0, 0);
        // 把end的hour设为1以便获取end对应年份的数据
        end = LocalDateTime.of(end.getYear(), 1, 1, 1, 0);

        List<TopicVO> topicVOList = new ArrayList<>();

        // 一月一次调用getTopNTags函数，获取每年的topN标签
        while (start.isBefore(end)) {
            LocalDateTime finalStart = start;
            LocalDateTime finalEnd = start.plusYears(1);
            List<TopicVO> topicVOS = getTopNTags(n, finalStart, finalEnd);
            topicVOS.forEach(topicVO -> topicVO.setTime(finalStart));

            topicVOList.addAll(topicVOS);

            start = finalEnd;
        }

        return topicVOList;
    }
}
