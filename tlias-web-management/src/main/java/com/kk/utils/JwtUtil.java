package com.kk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类。
 * 密钥与有效期通过 application.yml（tlias.jwt.*）配置，支持环境变量覆盖；
 * 保留静态方法供 Filter / Service 调用，配置在组件初始化时注入静态字段。
 */
@Component
public class JwtUtil {

    @Value("${tlias.jwt.secret}")
    private String secret;

    @Value("${tlias.jwt.expiration}")
    private long expiration;

    private static SecretKey KEY;
    private static long EXPIRATION_TIME;

    @PostConstruct
    public void init() {
        KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        EXPIRATION_TIME = expiration;
    }

    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
