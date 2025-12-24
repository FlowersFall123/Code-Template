package com.example.backend.service;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.entity.dto.RegisterForm;
import com.example.backend.entity.po.User;

import java.util.List;

public interface UserService {
    /**
     * 登录验证
     * @param account
     * @param password
     * @return
     */
    User loginCheck(String email, String password);
    /**
     * 注册用户
     * @param registerForm
     * @return
     */
    Integer register(RegisterForm registerForm);

    /**
     * 通过ID获取用户
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * 获取所有用户
     * @return
     */
    List<User> getAllUser();
}
