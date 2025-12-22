package com.springCloud.common.interceptors;

/*
 * @Auther:fz
 * @Date:2025/10/5
 * @Description:
 */

import cn.hutool.core.util.StrUtil;

import com.springCloud.common.constant.BaseContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取登陆用户信息
        String userId = request.getHeader("userId");
        log.info("拦截到userId:{}", userId);
        //2.判断是否获取了用户，如果就存到ThreadLocal
        if(StrUtil.isNotBlank(userId)){
            BaseContext.setCurrentId(Long.valueOf(userId));
        }
        //3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //4.清理ThreadLocal
        BaseContext.removeCurrentId();
    }
}
