package com.example.backend.controller.auth;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.LoginForm;
import com.example.backend.entity.dto.RegisterForm;
import com.example.backend.entity.dto.VerifyEmail;
import com.example.backend.entity.po.User;
import com.example.backend.jwt.JWTUtil;
import com.example.backend.service.UserService;
import com.example.backend.tool.VerifyCodeTool;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    VerifyCodeTool verifyCodeTool;

    @PostMapping("/login")
    public RestBean<String> login(@RequestBody LoginForm loginForm) {
        User user=userService.loginCheck(loginForm.getEmail(), loginForm.getPassword());
        if(user==null) return RestBean.failure(502,"账号或密码错误");
        else
        {
            user.setPassword("*");
            return RestBean.success("cg", JWTUtil.createToken(user));
        }
    }

    @PostMapping("/register")
    public RestBean<String> register(@RequestBody RegisterForm registerForm) {
        Integer result=userService.register(registerForm);
        if(result==1) return RestBean.success("注册成功");
        else if(result==-1) return RestBean.failure(502,"用户已存在");
        else return RestBean.failure(502,"注册失败");
    }

    @PostMapping("/sendCode/{email}")
    public RestBean<String> sendCode(@PathVariable("email") String email) {
        verifyCodeTool.sendEmailWithCheck( email);
        return RestBean.success("发送成功");
    }

    @PostMapping("/verifyCode")
    public RestBean<String> verifyCode(@RequestBody VerifyEmail verifyEmail) {
        if(verifyCodeTool.verifyCode(verifyEmail.getEmail(), verifyEmail.getCode())) return RestBean.success("验证成功");
        else return RestBean.failure(502,"验证失败");
    }
}
