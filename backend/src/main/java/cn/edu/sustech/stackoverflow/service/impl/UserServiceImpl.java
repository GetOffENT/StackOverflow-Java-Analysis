package cn.edu.sustech.stackoverflow.service.impl;

import cn.edu.sustech.stackoverflow.entity.User;
import cn.edu.sustech.stackoverflow.mapper.UserMapper;
import cn.edu.sustech.stackoverflow.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
