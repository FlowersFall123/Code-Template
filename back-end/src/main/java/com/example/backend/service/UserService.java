package com.example.backend.service;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.example.backend.entity.po.User;

import java.util.List;

public interface UserService {
    User getUserByAccountAndPassword(String account, String password);
    User getUserById(Long id);
    List<User> getAllUser();
}
