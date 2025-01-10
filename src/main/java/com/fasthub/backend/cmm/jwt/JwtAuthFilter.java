package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.oper.auth.service.CoustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

//    private final JwtUtil jwtUtil;
//    private final CoustomUserDetailService customUserDetailsService;
    private final JwtService authService;
    private final CoustomUserDetailService coustomUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authorizationHeader = request.getHeader("Authorization");
//        System.out.println("authorizationHeader : " + authorizationHeader);
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
//            System.out.println("authorizationHeader : " + authorizationHeader);
//            String token = authorizationHeader.substring(7);
//            if (jwtUtil.validateToken(token)) {
//                String userId = jwtUtil.getUserId(token);
//                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
//
//                if (userDetails != null){
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                }
//            }
//
//        }


        String accessToken = authService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        if (authService.validateAccessToken(accessToken)){
            System.out.println("accessToke in JwtFilter : " + accessToken);
            filterChain.doFilter(request,response);
            return;
        }

        String refreshToken = authService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);
//        User user = findUserByRefreshToken(refreshToken);



        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }
}
