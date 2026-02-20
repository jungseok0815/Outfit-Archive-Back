package com.fasthub.backend.user.auth.controller;

import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import com.fasthub.backend.user.usr.dto.LoginDto;
import com.fasthub.backend.user.usr.dto.LoginResponseDto;
import com.fasthub.backend.user.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(loginDto, response));
    }

    @GetMapping("/validate")
    public ResponseEntity<CustomUserDetails> vailData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : " + authentication.getName());
        if (!Objects.equals(authentication.getName(), "anonymousUser")) {
            log.info("authentication principal : " + authentication.getPrincipal());
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(customUserDetails);
        }
        return ResponseEntity.status(401).build();
    }
}
