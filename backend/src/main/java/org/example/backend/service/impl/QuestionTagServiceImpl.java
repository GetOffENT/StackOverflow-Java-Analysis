package org.example.backend.service.impl;

import org.example.backend.entity.QuestionTag;
import org.example.backend.mapper.QuestionTagMapper;
import org.example.backend.service.QuestionTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 问题标签关联表 服务实现类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@Service
public class QuestionTagServiceImpl extends ServiceImpl<QuestionTagMapper, QuestionTag> implements QuestionTagService {

}
