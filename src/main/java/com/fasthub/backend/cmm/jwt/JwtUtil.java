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
//    private final Key key;
//    private final long accessTokenExpTime;

//    public JwtUtil(
//            @Value("${jwt.secret}")
//            String secretKey,
//            @Value("${jwt.expiration_time}")
//            long accessTokenExpTime
//    ) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.accessTokenExpTime = accessTokenExpTime;
//    }
//
//    /**
//     * Access Token 생성
//     * @param member
//     * @return Access Token String
//     */
//    public String createAccessToken(CustomUserInfoDto member) {
//        return createToken(member, accessTokenExpTime);
//    }
//
//    /**
//     * JWT 생성
//     * @param member
//     * @param expireTime
//     * @return JWT String
//     */
//    private String createToken(CustomUserInfoDto member, long expireTime) {
//        Claims claims = Jwts.claims();
//        claims.put("userId", member.getUserId());
//        claims.put("userNm", member.getUserNm());
//        claims.put("role", member.getRole());
//
//        ZonedDateTime now = ZonedDateTime.now();
//        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);
//
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(Date.from(now.toInstant()))
//                .setExpiration(Date.from(tokenValidity.toInstant()))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    /**
//     * Token에서 User ID 추출
//     * @param token
//     * @return User ID
//     */
//    public String getUserId(String token) {
//        return parseClaims(token).get("userId", String.class);
//    }

//    /**
//     * JWT 검증
//     * @param token
//     * @return IsValidate
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        }
//        return false;
//    }


    //Token의 상태를 확인하는 매소드
    public TokenStatus getTokenStatus(String token, Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.AUTHENTICATED;
        } catch (ExpiredJwtException | IllegalArgumentException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException e) {
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    // 쿠키에서 원하는 토큰을 찾는다!
    public String resolveTokenFromCookie(Cookie[] cookies, JwtRule tokenPrefix) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenPrefix.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
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




//    /**
//     * JWT Claims 추출
//     * @param accessToken
//     * @return JWT Claims
//     */
//    public Claims parseClaims(String accessToken) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        }
//    }

}
