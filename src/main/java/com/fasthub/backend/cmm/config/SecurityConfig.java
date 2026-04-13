package com.fasthub.backend.cmm.config;

import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import com.fasthub.backend.cmm.jwt.JwtAuthFilter;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final AdminMemberRepository adminMemberRepository;

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/css/**","/js/**", "/img/**").permitAll()
                        .requestMatchers("/api/usr/login", "/api/usr/join", "/api/usr/logout", "/api/img/**").permitAll()
                        .requestMatchers("/api/usr/forgot-password", "/api/usr/reset-password", "/api/usr/find-id").permitAll()
                        .requestMatchers("/api/recommend/**").permitAll()
                        .requestMatchers("/api/admin/auth/login", "/api/admin/auth/logout").permitAll()
                        .requestMatchers("/api/usr/product/list", "/api/usr/product/**").permitAll()
                        .requestMatchers("/api/usr/brand/list", "/api/usr/brand/**").permitAll()
                        .requestMatchers("/api/usr/post/list", "/api/usr/post/search", "/api/usr/post/product/**", "/api/usr/post/user/**").permitAll()
                        .requestMatchers("/api/usr/banner/list").permitAll()
                        .requestMatchers("/api/admin/product/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "PARTNER")
                        .requestMatchers("/api/admin/order/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "PARTNER")
                        .requestMatchers("/api/admin/review/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN", "PARTNER")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/api/usr/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(
                            (request, response, authException) -> {
                                // 인증되지 않은 사용자 접근시 401 반환
                                log.info("로그인 안함!");
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"need login.\"}");
                            }
                    );
                    exception.accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                // 권한이 없는 사용자 접근시 403 반환
                                log.info("권한이 없음!");
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"not auth.\"}");
                            }
                    );
                })
                .cors(c -> {
                        CorsConfigurationSource source = request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.addAllowedOrigin(allowedOrigin);
                            config.addAllowedMethod("*");
                            config.addAllowedHeader("Authorization");
                            config.addAllowedHeader("Content-Type");
                            config.addAllowedHeader("X-Requested-With");
                            config.addAllowedHeader("Accept");
                            config.addAllowedHeader("Cookie");
                            config.addExposedHeader("Set-Cookie");
                            config.setAllowCredentials(true);
                            return config;
                        };
                    c.configurationSource(source);
                })
                .addFilterBefore(new JwtAuthFilter(jwtService, authRepository, adminMemberRepository), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                http.formLogin(AbstractHttpConfigurer::disable);
                http.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }
}
