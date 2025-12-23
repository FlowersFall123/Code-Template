package com.example.backend.service;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.entity.po.User;

import java.util.List;

public interface UserService {
    /**
     * 通过账号和密码获取用户
     * @param account
     * @param password
     * @return
     */
    User getUserByAccountAndPassword(String account, String password);
    /**
     * 注册用户
     * @param user
     * @return
     */
    Integer register(User user);

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
