package com.springCloud.api.config;

import com.springCloud.common.constant.BaseContext;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DefaultFeignConfig {

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return template -> {
            // 获取当前用户ID
            Long userId = BaseContext.getCurrentId();
            if (userId == null) {
                // 未登录或非用户请求，直接跳过
                return;
            }
            // 将 userId 放入请求头，传递给下游服务
            template.header("userId", userId.toString());
            log.info("Feign 请求拦截器添加 userId 请求头：{}", userId);
        };
    }

    @Bean
    public SpringDecoder feignDecoder() {
        // 添加更多支持的转换器
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        // 支持 JSON
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, // 显式支持 octet-stream
                MediaType.TEXT_PLAIN
        ));
        converters.add(jacksonConverter);

        // 支持文本
        converters.add(new StringHttpMessageConverter());

        ObjectFactory<HttpMessageConverters> factory = () ->
                new HttpMessageConverters(converters);

        return new SpringDecoder(factory);
    }
}
