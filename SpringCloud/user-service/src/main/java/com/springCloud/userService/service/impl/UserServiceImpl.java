package com.springCloud.userService.service.impl;



import com.springCloud.api.entity.User;
import com.springCloud.common.constant.BaseContext;
import com.springCloud.userService.mapper.UserMapper;
import com.springCloud.userService.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;


    @Override
    public User getUserByAccountAndPassword(String account, String password) {
        return userMapper.getUserByAccountAndPassword(account,password);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public Integer UpdateUser(User user) {
        user.setId(BaseContext.getCurrentId());
        return userMapper.UpdateUser(user);
    }


}
