package com.springCloud.userService.service;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */





import com.springCloud.api.entity.User;

import java.util.List;

public interface UserService {
    User getUserByAccountAndPassword(String account, String password);
    User getUserById(Long id);

}
