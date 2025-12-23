package com.example.backend.service.Impl;

import cn.hutool.core.util.IdUtil;
import com.example.backend.entity.po.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;


    @Override
    public User getUserByAccountAndPassword(String account, String password) {
        return userMapper.getUserByAccountAndPassword(account,password);
    }

    @Override
    public Integer register(User user) {
        //生成账号
        String account = IdUtil.getSnowflake(1, 1).nextIdStr();
        return 0;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }
}
