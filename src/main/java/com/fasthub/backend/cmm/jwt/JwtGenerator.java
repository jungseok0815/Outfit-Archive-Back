package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.admin.auth.entity.AdminMember;
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

    // Admin Access Token 생성
    // subject: adminMember.getId() (DB PK) → 이후 관리자 조회 시 사용
    // claims: userType="ADMIN", memberId, memberNm → Admin/User 분기 판단에 사용
    public String generateAccessToken(final Key ACCESS_SECRET, final long ACCESS_EXPIRATION, AdminMember adminMember) {
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createAdminClaims(adminMember))
                .setSubject(String.valueOf(adminMember.getId()))
                .setExpiration(new Date(now + ACCESS_EXPIRATION))
                .signWith(ACCESS_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    // Admin Refresh Token 생성
    // subject: adminMember.getMemberId() (String 아이디) → 만료 시 DB 관리자 조회에 사용
    // claims: userType="ADMIN" → Refresh 재발급 시 Admin/User 분기 판단에 사용
    public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_EXPIRATION, AdminMember adminMember) {
        Long now = System.currentTimeMillis();
        Claims claims = Jwts.claims();
        claims.put("userType", "ADMIN");
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(claims)
                .setSubject(adminMember.getMemberId())
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

    // Admin JWT Payload(Claims) 생성
    // userType="ADMIN" claim 포함 → JwtService.getAuthentication()에서 Admin/User 분기 판단에 사용
    private Map<String, Object> createAdminClaims(AdminMember adminMember) {
        Claims claims = Jwts.claims();
        claims.put("userType", "ADMIN");
        claims.put("memberId", adminMember.getMemberId());
        claims.put("memberNm", adminMember.getMemberNm());
        return claims;
    }
}
