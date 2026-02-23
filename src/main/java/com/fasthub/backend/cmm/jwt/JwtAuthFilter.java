package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthRepository authRepository;

    /**
     * 특정 url에는 해당 되지 않도록!
     * @param request
     * @return
     * @throws ServletException
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/usr/login") || path.startsWith("/api/usr/join");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * 쿠기에서 담겨서 전달되는 token을 가져온다.
         */
        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);

        if (accessToken == null && refreshToken == null){
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request,response);
            return;
        }

        /**
         * access token토큰 유효성 검사
         */
        if (jwtService.validateAccessToken(accessToken)) {
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        /**
         * refresh token 유효성 검사
         */
        User user = findUserByRefreshToken(refreshToken);
        if (jwtService.validateRefreshToken(refreshToken, user.getUserId())) {
            String reissuedAccessToken = jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);
            setAuthenticationToContext(reissuedAccessToken);
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }


    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User findUserByRefreshToken(String refreshToken){
        String identifier = jwtService.getIdentifierFromRefresh(refreshToken);
        return  authRepository.findByUserId(identifier).get();
    }



}
