package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.cmm.enums.TokenStatus;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.exception.BusinessException;
import com.fasthub.backend.oper.auth.dto.CustomUserDetails;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import com.fasthub.backend.oper.auth.service.CoustomUserDetailService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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

import static com.fasthub.backend.cmm.enums.ErrorCode.NOT_AUTHENTICATED_USER;
import static com.fasthub.backend.cmm.enums.ErrorCode.NOT_REFRESG_KEY;
import static com.fasthub.backend.cmm.enums.JwtRule.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class JwtService {

    private final AuthRepository authRepository;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CoustomUserDetailService coustomUserDetailService;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;


    public JwtService(
            CoustomUserDetailService coustomUserDetailService,
            JwtGenerator jwtGenerator,
            JwtUtil jwtUtil,
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
            @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
            @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
            @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION
    ) {
        this.coustomUserDetailService = coustomUserDetailService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }

    /**
     * 사용자의 권한 상태 확인,
     * @param requestUser
     */
    public void validateUser(User requestUser) {
        if (requestUser.getAuthName() == UserRole.NOT_REGISTERED) {
            throw new BusinessException(NOT_AUTHENTICATED_USER);
        }
    }

    /**
     * AccessToken 생성
     * @param response
     * @param requestUser
     * @return
     */
    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return accessToken;
    }

    /**
     * refresh 토근 생성
     * @param response
     * @param requestUser
     * @return
     */
    @Transactional
    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());
        return refreshToken;
    }

    /**
     * 쿠기
     * @param tokenPrefix
     * @param token
     * @param maxAgeSeconds
     * @return
     */
    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(true)
                .build();
    }

    /**
     * 쿠기에서 토큰 추출
     * @param request
     * @param tokenPrefix
     * @return
     */
    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("cookies is null");
        }
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    /**
     * AccessToken 유효성 검사
     * @param token
     * @return
     */
    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    /**
     * RefreshToken 유효성 검사 <추가 작업 필요!>
     * @param token
     * @param identifier
     * @return
     */
    public boolean validateRefreshToken(String token, String identifier) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;
//        Token storedToken = tokenRepository.findByIdentifier(identifier);
//        boolean isTokenMatched = storedToken.getToken().equals(token);
        return isRefreshValid;
    }


    /**
     *
     * @param token
     * @return
     */
    public Authentication getAuthentication(String token) {
        CustomUserDetails principal = coustomUserDetailService.loadUserByUsername(getUserPk(token, ACCESS_SECRET_KEY));
        log.info("principal11111 : " + principal);
        log.info("principal22222 : " + principal.getAuthorities());
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private String getUserPk(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

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
