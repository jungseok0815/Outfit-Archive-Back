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


@Slf4j
@Component
public class JwtUtil {
    /**
     * Token의 상태를 확인하는 매소드
     * @param token
     * @param secretKey
     * @return
     */
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

    /**
     * 쿠기 조회해서 원하는 쿠키를 찾음
     * @param cookies
     * @param tokenPrefix
     * @return
     */
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

    public Key getSigningKey(String secretKey) {
        String encodedKey = encodeToBase64(secretKey);
        return Keys.hmacShaKeyFor(encodedKey.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeToBase64(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Cookie resetToken(JwtRule tokenPrefix) {
        Cookie cookie = new Cookie(tokenPrefix.getValue(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }


}
