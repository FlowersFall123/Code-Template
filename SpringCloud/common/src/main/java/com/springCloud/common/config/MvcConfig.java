package com.springCloud.common.config;


import com.springCloud.common.interceptors.UserInfoInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;


@Slf4j
@Configuration
@ConditionalOnWebApplication(type = SERVLET) // 微服务使用，网关不使用
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 添加用户信息拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("-----------MvcConfig add UserInfoInterceptor-----------");
        registry.addInterceptor(new UserInfoInterceptor());
    }


}