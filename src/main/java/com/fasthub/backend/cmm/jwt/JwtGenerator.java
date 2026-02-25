package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.user.usr.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// JWT 토큰 생성 전담 컴포넌트
// JwtService에서 호출되며 실제 토큰 문자열을 만들어 반환
@Component
public class JwtGenerator {

    // Access Token 생성
    // subject: user.getId() (DB PK, Long) → 이후 유저 조회 시 사용
    // claims: userId, userNm → Payload에 포함 (누구나 읽기 가능, 민감정보 금지)
    // signWith: ACCESS_SECRET_KEY로 HMAC-SHA256 서명 → 위변조 방지
    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, User user) {
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(new Date(now + ACCESS_EXPIRATION))
                .signWith(ACCESS_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    // subject: user.getUserId() (String 아이디) → 만료 시 DB에서 유저 조회에 사용
    // claims 없음: Access Token과 달리 유저 정보를 최소화하여 탈취 피해 축소
    // signWith: REFRESH_SECRET_KEY로 서명 → Access Token과 키를 분리해 독립적으로 검증
    public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_EXPIRATION, User user) {
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(user.getUserId())
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(REFRESH_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT Header 생성
    // typ: 토큰 타입 (JWT)
    // alg: 서명 알고리즘 (HS256 = HMAC-SHA256)
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    // JWT Payload(Claims) 생성
    // 토큰에 담을 유저 식별 정보 → Base64 인코딩으로 누구나 읽기 가능
    // 비밀번호 등 민감한 정보는 절대 포함하면 안 됨
    private Map<String, Object> createClaims(User user) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getUserId());
        claims.put("userNm", user.getUserNm());
        return claims;
    }
}
