package com.example.backend.controller.user;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.po.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/getUserById")
    public RestBean<User>getUserById(HttpServletRequest request)
    {
        User user=userService.getUserById((Long) request.getAttribute("id"));
        user.setPassword("*");
        log.info("用户ID为{}",user.getId());
        return RestBean.success("cg",user);
    }
}
