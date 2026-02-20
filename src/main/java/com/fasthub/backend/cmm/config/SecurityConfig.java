package com.fasthub.backend.cmm.config;

import com.fasthub.backend.cmm.jwt.JwtAuthFilter;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final AuthRepository authRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**","/js/**", "/img/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/img/**","/api/usr/insert").permitAll()
                        .requestMatchers("/api/product/select", "/api/product/list").permitAll()
                        .requestMatchers("/api/product/delete", "/api /product/update", "/api/product/insert").hasRole("ADMIN")
                        .requestMatchers("/api/brand/delete", "/api /brand/update", "/api/brand/insert").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(
                            (request, response, authException) -> {
                                // 인증되지 않은 사용자 접근시 401 반환
                                System.out.println("로그인 안함!");
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"need login.\"}");
                            }
                    );
                    exception.accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                // 권한이 없는 사용자 접근시 403 반환
                                System.out.println("권한이 없음!");
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"not auth.\"}");
                            }
                    );
                })
                .cors(c -> {
                        CorsConfigurationSource source = request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.addAllowedOrigin("http://localhost:3002"); // 허용할 도메인
                            config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
                            config.addAllowedHeader("*"); // 모든 헤더 허용
                            config.setAllowCredentials(true); // 쿠키 허용
                            return config;
                        };
                    c.configurationSource(source);
                })
                .addFilterBefore(new JwtAuthFilter(jwtService,authRepository), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                http.formLogin(AbstractHttpConfigurer::disable);
                http.httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }
}
