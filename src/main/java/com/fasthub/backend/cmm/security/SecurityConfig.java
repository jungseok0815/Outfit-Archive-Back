package com.fasthub.backend.cmm.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers( "/usr/insert" , "/").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/buyer").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/usr/**").hasAnyRole("USER","ADMIN","MANGER")
                        .anyRequest().authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin
                                .usernameParameter("userId")
                                .passwordParameter("userPwd")
                                .loginProcessingUrl("/usr/login")
                                .successHandler(new AuthenticationSuccessHandler() {
                                    @Override
                                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                        System.out.println("login 성공");
                                        SecurityContext context = SecurityContextHolder.getContext();
                                        Authentication authentication1 = context.getAuthentication();
                                        System.out.println("authentication1 : " + authentication1.getName());
                                        System.out.println("authoriteies : " + authentication1.getAuthorities());
                                    }
                                })
                                .failureForwardUrl("/login/fail")
                );

        http.csrf(csrf ->csrf.ignoringRequestMatchers(PathRequest.toH2Console())
                        .disable())
                .headers(headers ->headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        //권한에 맞지 않을 경우
        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/usr/accessDenied"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
