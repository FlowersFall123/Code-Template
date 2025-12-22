package com.springCloud.userService.controller;


import com.springCloud.api.entity.User;
import com.springCloud.common.constant.RestBean;
import com.springCloud.common.jwt.JWTUtil;
import com.springCloud.userService.entity.dto.LoginForm;
import com.springCloud.userService.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@RestController
@RequestMapping("/api/auth")
@Api("认证相关接口")
public class AuthController {
    @Resource
    UserService userService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public RestBean<String> login(@RequestBody LoginForm loginForm) {
        User user=userService.getUserByAccountAndPassword(loginForm.getAccount(), loginForm.getPassword());
        if(user==null) return RestBean.failure(502,"账号或密码错误");
        else
        {
            user.setPassword("*");
            if (user.getStatus()==0)return RestBean.failure(502,"用户被禁用");
            else if (user.getStatus()==-1) return RestBean.failure(502,"用户不存在");
            else return RestBean.success("cg", JWTUtil.createToken(user.getId(),user.getAccount()));
        }
    }

}
