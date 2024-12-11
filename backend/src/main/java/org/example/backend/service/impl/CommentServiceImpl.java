package org.example.backend.service.impl;

import org.example.backend.entity.Comment;
import org.example.backend.mapper.CommentMapper;
import org.example.backend.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论信息表 服务实现类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
