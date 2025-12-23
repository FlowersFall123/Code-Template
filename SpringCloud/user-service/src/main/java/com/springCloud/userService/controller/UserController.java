package com.springCloud.userService.controller;


import com.springCloud.api.entity.User;
import com.springCloud.common.constant.BaseContext;
import com.springCloud.common.constant.RestBean;
import com.springCloud.userService.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@Api("用户相关接口")
public class UserController {
    @Resource
    UserService userService;

    @GetMapping("/getUserById")
    @ApiOperation("根据ID获取用户信息")
    public RestBean<User> getUserById() {
        log.info("用户的ID为{}", BaseContext.getCurrentId());
        User user=userService.getUserById(BaseContext.getCurrentId());
        if (user!=null)user.setPassword(null);
        return RestBean.success("cg",user);
    }

    @GetMapping("/getOtherUserById/{userId}")
    @ApiOperation("根据ID获取其他用户信息")
    public RestBean<User> getOtherUserById(@PathVariable Long userId) {
        User user=userService.getUserById(userId);
        if (user!=null)user.setPassword(null);
        return RestBean.success("cg",user);
    }

    @PostMapping("/updateUser")
    @ApiOperation("更新用户信息")
    public RestBean<String> updateUser(@RequestBody User user) {
        userService.UpdateUser(user);
        return RestBean.success("用户信息更新成功");
    }

}
