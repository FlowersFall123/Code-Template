package com.springCloud.userService;

import com.springCloud.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ServletComponentScan
@SpringBootApplication
@EnableFeignClients(basePackages = "com.springCloud.api.client",
        defaultConfiguration = DefaultFeignConfig.class)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
