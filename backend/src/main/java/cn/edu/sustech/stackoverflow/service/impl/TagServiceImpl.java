package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.Question;
import cn.edu.sustech.stackoverflow.entity.QuestionTag;
import cn.edu.sustech.stackoverflow.entity.Tag;
import cn.edu.sustech.stackoverflow.entity.vo.TagVO;
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
     * 获取指定时间段内的前n个热门标签
     *
     * @param n     前n个
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 前n个热门标签
     */
    @Override
    public List<TagVO> getTopNTags(Integer n, LocalDateTime startTime, LocalDateTime endTime) {

        List<TagVO> tagVOS;
        if (startTime == null && endTime == null) {
            List<Tag> tagList = tagMapper.selectList(null);

        // 按照count排序
        tagList.sort((o1, o2) -> o2.getCount() - o1.getCount());

        Integer count = tagList.stream().map(Tag::getCount).reduce(Integer::sum).orElse(0);

            tagVOS = BeanUtil.copyToList(tagList, TagVO.class);

            tagVOS.forEach(tagVO -> tagVO.setPercentage((double) tagVO.getCount() / count));
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
            List<QuestionTag> questionTags = questionTagMapper.selectList(
                    new LambdaQueryWrapper<QuestionTag>()
                            .in(QuestionTag::getQuestionId, questionIds)
            );

            // 统计每个tag出现的次数
            Map<Long, Long> tagCount = questionTags.stream().collect(Collectors.groupingBy(QuestionTag::getTagId, Collectors.counting()));

            int totalCount = questionTags.size();

            // tagId, 并去重
            List<Long> tagIds = questionTags.stream().map(QuestionTag::getTagId).distinct().toList();
            List<Tag> tags = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>()
                            .in(Tag::getTagId, tagIds)
            );

            tagVOS = tags.stream().map(tag -> {
                int count = tagCount.getOrDefault(tag.getTagId(), 0L).intValue();
                return TagVO.builder()
                        .tagId(tag.getTagId())
                        .tagName(tag.getTagName())
                        .count(count)
                        .percentage((double) count / totalCount)
                        .build();
            }).sorted(Comparator.comparing(TagVO::getCount).reversed()).toList();
        }

        if (tagVOS.size() > n) {
            return tagVOS.subList(0, n);
        } else {
            return tagVOS;
        }
    }
}
