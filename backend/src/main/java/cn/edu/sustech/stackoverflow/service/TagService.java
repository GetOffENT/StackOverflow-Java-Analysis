package cn.edu.sustech.stackoverflow.service;

import cn.edu.sustech.stackoverflow.entity.Tag;
import cn.edu.sustech.stackoverflow.entity.vo.TagVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 标签信息表 服务类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取指定时间段内的前n个热门标签
     *
     * @param n     前n个
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 前n个热门标签
     */
    List<TagVO> getTopNTags(Integer n, LocalDateTime startTime, LocalDateTime endTime);
}
