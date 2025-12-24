package com.example.backend.tool;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class VerifyCodeTool {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String EMAIL_CACHE_PREFIX = "email:code:";

    @Value("${proPath.mail.qq}")
    private String fromMail;

    // 前缀定义
    private static final String LOCK_PREFIX = "email:lock:"; // 60秒锁定
    private static final String CODE_PREFIX = "email:code:"; // 验证码存储

    public void sendEmailWithCheck(String email) {
        // 1. 频率检查：判断 60 秒内是否发过
        String lockKey = LOCK_PREFIX + email;
        Boolean isLocked = redisTemplate.hasKey(lockKey);
        if (isLocked) {
            throw new RuntimeException("请求太频繁，请60秒后再试");
        }

        // 2. 旧验证码检查：判断 5 分钟内是否发过
        String codeKey = CODE_PREFIX + email;
        Boolean hasOldCode = redisTemplate.hasKey(codeKey);
        if (hasOldCode) {
            redisTemplate.delete(codeKey);
        }

        // 3. 生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 4. 原子操作：存入验证码 (5分钟有效) 并 设置发送锁 (60秒过期)
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(lockKey, "1", 60, TimeUnit.SECONDS);

        // 5. 异步调用邮件发送（最耗时的步骤交给线程池）
        this.doSendRealEmail(email, code);
    }

    @Async("mailExecutor") // 关键：异步执行
    public void doSendRealEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromMail);
            message.setTo(email);
            message.setSubject("验证码校验");
            message.setText("您的验证码是：" + code + "，有效期5分钟。");

            mailSender.send(message);
            System.out.println("邮件发送成功：" + email);
        } catch (Exception e) {
            // 异步任务报错需要记录日志，防止静默失败
            System.err.println("邮件发送异常：" + e.getMessage());
        }
    }

    // 校验验证码
    public boolean verifyCode(String email, String inputCode) {
        String cacheCode = redisTemplate.opsForValue().get(EMAIL_CACHE_PREFIX + email);
        if (cacheCode != null && cacheCode.equals(inputCode)) {
            redisTemplate.delete(EMAIL_CACHE_PREFIX + email);
            return true;
        }
        return false;
    }
}
