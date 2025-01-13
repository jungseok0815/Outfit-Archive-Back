package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import com.fasthub.backend.oper.auth.service.AuthService;
import com.fasthub.backend.oper.auth.service.CoustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthRepository authRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);

        if (accessToken == null && refreshToken == null){
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request,response);
            return;
        }

        if (jwtService.validateAccessToken(accessToken)) {
            System.out.println("accessToke in JwtFilter : " + accessToken);
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

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
