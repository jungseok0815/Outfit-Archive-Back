package com.fasthub.backend.user.usr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

// Redis 기반 비밀번호 재설정 토큰 관리
// Key: "pwreset:{token}", Value: userId (이메일), TTL: 10분
@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "pwreset:";
    private static final long TOKEN_TTL_MINUTES = 10;

    public String generateToken(String userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue()
                .set(PREFIX + token, userId, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    // 토큰으로 userId 조회 (없거나 만료 시 null 반환)
    public String getUserId(String token) {
        return redisTemplate.opsForValue().get(PREFIX + token);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
