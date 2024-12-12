package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.service.QuestionService;
import lombok.RequiredArgsConstructor;
import cn.edu.sustech.stackoverflow.entity.Question;
import cn.edu.sustech.stackoverflow.mapper.QuestionMapper;
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
