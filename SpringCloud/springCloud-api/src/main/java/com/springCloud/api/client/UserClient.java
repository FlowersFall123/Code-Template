package com.springCloud.api.client;


import com.springCloud.api.entity.User;
import com.springCloud.common.constant.RestBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserClient {
    @PostMapping("/updateUser")
    RestBean<String> updateUser(@RequestBody User user);

    @GetMapping("/getUserById")
    RestBean<User> getUserById();

    @GetMapping("/getOtherUserById/{userId}")
    RestBean<User> getOtherUserById(@PathVariable Long userId);
}