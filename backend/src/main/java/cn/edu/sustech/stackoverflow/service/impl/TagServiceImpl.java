package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.Tag;
import cn.edu.sustech.stackoverflow.entity.vo.TagVO;
import cn.edu.sustech.stackoverflow.service.TagService;
import cn.edu.sustech.stackoverflow.mapper.TagMapper;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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


    /**
     * 获取前n个热门标签
     *
     * @param n 前n个
     * @return 前n个热门标签
     */
    @Override
    public List<TagVO> getTopNTags(Integer n) {

        List<Tag> tagList = tagMapper.selectList(null);

        // 按照count排序
        tagList.sort((o1, o2) -> o2.getCount() - o1.getCount());

        Integer count = tagList.stream().map(Tag::getCount).reduce(Integer::sum).orElse(0);

        List<TagVO> tagVOS = BeanUtil.copyToList(tagList, TagVO.class);

        tagVOS.forEach(tagVO -> tagVO.setPercentage((double) tagVO.getCount() / count));

        if (tagVOS.size() > n) {
            return tagVOS.subList(0, n);
        } else {
            return tagVOS;
        }
    }
}
