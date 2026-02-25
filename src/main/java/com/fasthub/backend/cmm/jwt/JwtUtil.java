package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.cmm.enums.TokenStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;


// JWT 저수준 유틸 컴포넌트
// 토큰 파싱, 키 생성, 쿠키 조작 등 라이브러리 직접 호출을 담당
// JwtService에서 사용
@Slf4j
@Component
public class JwtUtil {

    // 토큰의 현재 상태를 반환
    // parseClaimsJws() 한 번 호출로 서명 검증 + 만료 확인 + 형식 검사를 모두 수행
    // 정상 → AUTHENTICATED / 만료 → EXPIRED / 위변조·형식오류 → JwtException 발생
    public TokenStatus getTokenStatus(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            log.info("유효시간이 다 되었습니다.");
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    // 요청 쿠키 배열에서 원하는 이름(access / refresh)의 쿠키 값을 찾아 반환
    // 해당 쿠키가 없으면 null 반환
    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
        if (cookies != null){
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }

    // yml의 시크릿키 문자열 → HMAC-SHA256용 Key 객체로 변환
    // 변환 과정: 문자열 → Base64 인코딩 → UTF-8 byte[] → Keys.hmacShaKeyFor()
    // 시크릿키는 이 Key 객체 형태로만 사용되며 토큰에는 절대 포함되지 않음
    public Key getSigningKey(String secretKey) {
        String encodedKey = encodeToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    // 시크릿키 문자열을 Base64로 인코딩
    // HMAC 키 길이 요구사항(256bit 이상)을 충족시키기 위해 사용
    private String encodeToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // 토큰 쿠키를 초기화(삭제)하는 쿠키 반환
    // MaxAge=0 으로 설정하면 브라우저가 해당 쿠키를 즉시 삭제
    // 로그아웃 처리 시 사용
    public Cookie resetToken(JwtRule tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }


}
