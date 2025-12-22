package com.springCloud.common.jwt;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    private static final String SECRET = "your-secret-key"; // 加密密钥
    private static final long EXPIRATION = 86400000; // 1天有效期

    // 创建Token
    public static String createToken(Long userId,String account) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 7);
        return JWT.create()
                .withHeader(Map.of("alg", "HS256", "typ", "JWT"))
                .withClaim("id", userId)
                .withClaim("account", account)
                .withClaim("password", "secret")
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
    }

    // 验证Token
    public static Map<String, Claim> verifyToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(SECRET))
                    .build()
                    .verify(token)
                    .getClaims();
        } catch (Exception e) {
            logger.error("Token验证失败", e);
            return null;
        }
    }
}