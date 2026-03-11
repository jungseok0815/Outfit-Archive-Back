package com.fasthub.backend.cmm.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// Redis 기반 Refresh Token 저장소
// Key: "refresh:{userId}", Value: refreshToken 문자열
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "refresh:";

    // 로그인 시 Refresh Token 저장 (만료시간 자동 설정)
    public void save(String userId, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue()
                .set(PREFIX + userId, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    // 로그아웃 시 Refresh Token 삭제
    public void delete(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    // 요청의 Refresh Token과 Redis 저장값 일치 여부 확인
    // Refresh Token Rotation: 재발급 시 이전 토큰은 덮어씌워져 자동 무효화됨
    public boolean isValid(String userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return stored != null && stored.equals(refreshToken);
    }
}
