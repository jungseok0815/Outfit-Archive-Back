package com.fasthub.backend.cmm.security;

import com.fasthub.backend.oper.auth.dto.CustomUserDetails;
import com.fasthub.backend.oper.auth.service.CoustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CoustomUserDetailService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            System.out.println("authorizationHeader : " + authorizationHeader);
            String token = authorizationHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String userId = jwtUtil.getUserId(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

                if (userDetails != null){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        }
        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }
}
