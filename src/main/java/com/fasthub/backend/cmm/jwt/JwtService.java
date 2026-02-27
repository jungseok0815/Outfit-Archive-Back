package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.service.AdminCustomUserDetailService;
import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.cmm.enums.TokenStatus;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;

import com.fasthub.backend.user.usr.service.CustomUserDetailService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;


import static com.fasthub.backend.cmm.enums.JwtRule.*;
import static com.fasthub.backend.cmm.error.ErrorCode.*;


// JWT 관련 비즈니스 로직을 담당하는 서비스
// JwtGenerator(토큰 생성), JwtUtil(저수준 유틸)을 조합해서 실제 동작을 처리
@Service
@Slf4j
@Transactional(readOnly = true)
public class JwtService {

    private final AuthRepository authRepository;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final AdminCustomUserDetailService adminCustomUserDetailService;

    // application.yml의 시크릿키 문자열을 HMAC-SHA256용 Key 객체로 변환해서 보관
    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;
    private final boolean COOKIE_SECURE;


    public JwtService(
            CustomUserDetailService customUserDetailService,
            AdminCustomUserDetailService adminCustomUserDetailService,
            JwtGenerator jwtGenerator,
            JwtUtil jwtUtil,
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
            @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
            @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
            @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION,
            @Value("${jwt.cookie-secure}") boolean COOKIE_SECURE

    ) {
        this.customUserDetailService = customUserDetailService;
        this.adminCustomUserDetailService = adminCustomUserDetailService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.authRepository = authRepository;
        // yml의 문자열 시크릿키 → Base64 인코딩 → HMAC Key 객체로 변환
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
        this.COOKIE_SECURE = COOKIE_SECURE;
    }

    // 유저의 가입 승인 여부 확인
    // NOT_REGISTERED 상태면 서비스 이용 불가 처리
    public void validateUser(User requestUser) {
        if (requestUser.getAuthName() == UserRole.NOT_REGISTERED) {
            throw new BusinessException(NOT_AUTHENTICATED_USER);
        }
    }

    // Access Token 생성 후 HttpOnly 쿠키에 담아 응답 헤더에 추가
    // 로그인 성공 시 호출 → 클라이언트가 이후 요청마다 쿠키를 자동으로 전송
    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return accessToken;
    }

    // Refresh Token 생성 후 HttpOnly 쿠키에 담아 응답 헤더에 추가
    // Access Token 만료 시 재발급에 사용 (Refresh Token Rotation: 재발급 시 Refresh Token도 교체)
    @Transactional
    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return refreshToken;
    }

    // Admin Access Token 생성 후 HttpOnly 쿠키에 담아 응답 헤더에 추가
    public String generateAccessToken(HttpServletResponse response, AdminMember adminMember) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, adminMember);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return accessToken;
    }

    // Admin Refresh Token 생성 후 HttpOnly 쿠키에 담아 응답 헤더에 추가
    @Transactional
    public String generateRefreshToken(HttpServletResponse response, AdminMember adminMember) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, adminMember);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return refreshToken;
    }

    // 토큰을 HttpOnly 쿠키로 세팅
    // HttpOnly  → JS에서 접근 불가 (XSS 방어)
    // Secure    → HTTPS에서만 전송 (네트워크 감청 방어)
    // SameSite  → 외부 사이트 요청에 쿠키 미포함 (CSRF 방어)
    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(COOKIE_SECURE)
                .build();
    }

    // 요청 쿠키에서 토큰 이름(access / refresh)으로 토큰 값 추출
    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) log.info("cookies is null");
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    // Access Token 유효성 검사
    // ACCESS_SECRET_KEY로 서명 검증 → 위변조 및 만료 여부 확인
    public boolean validateAccessToken(String token) {
        log.info("valiDataResult : " + jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY));
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    // Refresh Token 유효성 검사
    // REFRESH_SECRET_KEY로 서명 검증 → Access Token과 별도 키를 사용해 독립적으로 검증
    public boolean validateRefreshToken(String token, String identifier) {
        log.info("refreshToken identifier : " + identifier);
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;
//        Token storedToken = tokenRepository.findByIdentifier(identifier);
//        boolean isTokenMatched = storedToken.getToken().equals(token);
        return isRefreshValid;
    }

    // Access Token에서 유저 정보를 꺼내 Spring Security 인증 객체 생성
    // → SecurityContextHolder에 등록되어 이후 컨트롤러에서 @AuthenticationPrincipal로 유저 정보 사용 가능
    // userType="ADMIN" claim이 있으면 AdminCustomUserDetailService, 없으면 일반 유저 서비스로 분기
    public Authentication getAuthentication(String token) {
        String pk = getUserPk(token, ACCESS_SECRET_KEY);
        String userType = getUserTypeFromToken(token, ACCESS_SECRET_KEY);
        UserDetails principal;
        if ("ADMIN".equals(userType)) {
            principal = adminCustomUserDetailService.loadUserByUsername(pk);
        } else {
            principal = customUserDetailService.loadUserByUsername(pk);
        }
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    // Refresh Token의 userType claim 추출 → JwtAuthFilter에서 Admin/User Refresh 재발급 분기에 사용
    // 파싱 실패 시 null 반환 (예외 전파 없음)
    public String getUserTypeFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .get("userType", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    // 토큰 Claims에서 userType claim 추출 (private 헬퍼)
    private String getUserTypeFromToken(String token, Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userType", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    // 토큰의 Payload에서 subject(유저 DB PK) 추출
    // secretKey로 서명을 검증한 뒤 subject를 반환 → 위변조된 토큰이면 여기서 예외 발생
    private String getUserPk(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Refresh Token의 subject(userId 문자열)를 추출
    // JwtAuthFilter에서 Refresh Token으로 DB 유저를 조회할 때 사용
    public String getIdentifierFromRefresh(String refreshToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BusinessException(NOT_REFRESG_KEY);
        }
    }


}
