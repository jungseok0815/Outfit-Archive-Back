package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.oper.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtGenerator {

    //Access Token생성
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

    //Refresh Token생성
    public String generateRefreshToken(final Key REFRESH_SECRET, final long REFRESH_EXPIRATION, User user) {
        Long now = System.currentTimeMillis();

        Map<String, Object> userIdentifier = new HashMap<>();
        userIdentifier.put("userId" , user.getUserId());
        userIdentifier.put("userNm",  user.getUserNm());
        userIdentifier.put("authName", user.getAuthName());

        return Jwts.builder()
                .setHeader(createHeader())
                .setSubject(user.getUserId())
                .setExpiration(new Date(now + REFRESH_EXPIRATION))
                .signWith(REFRESH_SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    // 헤더 생성
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    // 클래임 생성
    private Map<String, Object> createClaims(User user) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getUserId());
        claims.put("userNm", user.getUserNm());
        return claims;
    }
}
