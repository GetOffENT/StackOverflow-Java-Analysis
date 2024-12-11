package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Question;
import org.example.backend.mapper.QuestionMapper;
import org.example.backend.service.QuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 问题信息表 服务实现类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {


}
