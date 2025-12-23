package com.example.backend.controller.auth;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.LoginForm;
import com.example.backend.entity.po.User;
import com.example.backend.jwt.JWTUtil;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;



/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Resource
    UserService userService;

    @PostMapping("/login")
    public RestBean<String> login(@RequestBody LoginForm loginForm) {
        User user=userService.getUserByAccountAndPassword(loginForm.getAccount(), loginForm.getPassword());
        if(user==null) return RestBean.failure(502,"账号或密码错误");
        else
        {
            user.setPassword("*");
            return RestBean.success("cg", JWTUtil.createToken(user));
        }
    }


}
