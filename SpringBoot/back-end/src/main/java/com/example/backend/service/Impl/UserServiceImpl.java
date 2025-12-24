package com.example.backend.service.Impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.example.backend.common.UserIdentity;
import com.example.backend.common.UserStatus;
import com.example.backend.entity.dto.RegisterForm;
import com.example.backend.entity.po.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import com.example.backend.tool.VerifyCodeTool;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public User loginCheck(String email, String password) {
        User user = userMapper.getUserByEmail(email);
        //密码校验
        boolean isOk = BCrypt.checkpw(password, user.getPassword());
        if(isOk)return user;
        else return null;
    }

    @Override
    public Integer register(RegisterForm registerForm) {
        //生成账号
        String account = IdUtil.getSnowflake(1, 1).nextIdStr();
        //密码加密
        String hashed = BCrypt.hashpw(registerForm.getPassword(), BCrypt.gensalt());
        User newUser = User.builder()
                .username(registerForm.getUsername())
                .account(account)
                .password(hashed)
                .avatar("2003346691566211072.jpg")
                .email(registerForm.getEmail())
                .identity(UserIdentity.DEFAULT)
                .time(new Date())
                .status(UserStatus.DEFAULT)
                .build();
        Integer status=userMapper.register(newUser);
        if (status==1) return 1;
        else return 0;
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
