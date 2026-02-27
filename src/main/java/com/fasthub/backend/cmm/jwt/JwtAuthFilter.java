package com.fasthub.backend.cmm.jwt;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 인증 필터
// OncePerRequestFilter: 같은 요청에서 필터가 중복 실행되지 않도록 보장
// SecurityConfig에서 UsernamePasswordAuthenticationFilter 앞에 등록
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final AdminMemberRepository adminMemberRepository;

    // 필터를 적용하지 않을 경로 지정
    // 로그인·회원가입은 토큰 없이 접근해야 하므로 제외
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/usr/login") || path.startsWith("/api/usr/join")
                || path.startsWith("/api/admin/auth/login") || path.startsWith("/api/admin/auth/join");
    }

    // 매 요청마다 실행되는 JWT 검증 로직
    // 1) 쿠키에서 토큰 추출
    // 2) Access Token 유효 → SecurityContext 등록
    // 3) Access Token 만료 → Refresh Token으로 재발급 후 SecurityContext 등록
    // 4) 토큰 없음 → 인증 없이 다음 필터로 통과 (이후 Security 권한 설정에서 차단)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 access / refresh 토큰 추출
        String accessToken = jwtService.resolveTokenFromCookie(request, JwtRule.ACCESS_PREFIX);
        String refreshToken = jwtService.resolveTokenFromCookie(request, JwtRule.REFRESH_PREFIX);

        // 토큰이 둘 다 없으면 인증하지 않고 통과
        // → SecurityConfig의 authorizeHttpRequests에서 인증 여부에 따라 차단 처리
        if (accessToken == null && refreshToken == null){
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request,response);
            return;
        }

        // Access Token 유효성 검사
        // ACCESS_SECRET_KEY로 서명 검증 → 유효하면 SecurityContext에 인증 등록 후 통과
        if (jwtService.validateAccessToken(accessToken)) {
            setAuthenticationToContext(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        // Access Token 만료 시 Refresh Token으로 재발급 처리
        // userType 클레임으로 Admin/User 분기 후 각각 재발급 처리
        String userType = jwtService.getUserTypeFromRefresh(refreshToken);
        if ("ADMIN".equals(userType)) {
            AdminMember adminMember = findAdminByRefreshToken(refreshToken);
            if (jwtService.validateRefreshToken(refreshToken, adminMember.getMemberId())) {
                String reissuedAccessToken = jwtService.generateAccessToken(response, adminMember);
                jwtService.generateRefreshToken(response, adminMember);
                setAuthenticationToContext(reissuedAccessToken);
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            User user = findUserByRefreshToken(refreshToken);
            if (jwtService.validateRefreshToken(refreshToken, user.getUserId())) {
                String reissuedAccessToken = jwtService.generateAccessToken(response, user);
                jwtService.generateRefreshToken(response, user);
                setAuthenticationToContext(reissuedAccessToken);
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    // Access Token에서 인증 객체를 생성해 SecurityContextHolder에 저장
    // 이후 컨트롤러에서 @AuthenticationPrincipal로 유저 정보에 접근 가능
    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Refresh Token의 subject(userId 문자열)로 DB에서 유저를 조회
    // Access Token이 만료됐을 때 재발급 대상 유저를 특정하기 위해 사용
    private User findUserByRefreshToken(String refreshToken){
        String identifier = jwtService.getIdentifierFromRefresh(refreshToken);
        return authRepository.findByUserId(identifier).get();
    }

    // Refresh Token의 subject(memberId 문자열)로 DB에서 관리자를 조회
    private AdminMember findAdminByRefreshToken(String refreshToken) {
        String identifier = jwtService.getIdentifierFromRefresh(refreshToken);
        return adminMemberRepository.findByMemberId(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 관리자가 없습니다."));
    }


}
