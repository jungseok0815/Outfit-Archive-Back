package com.fasthub.backend.oper.auth.controller;

import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.LoginDto;
import com.fasthub.backend.oper.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(LoginDto loginDto){
        String token = authService.login(loginDto);
//
//        ResponseCookie cookie = ResponseCookie.from("accessToken",token)
//                .maxAge(7 * 24 * 60 * 60)
//                .path("/")
//                .secure(true)
//                .sameSite("None")
//                .httpOnly(true)
//                .build();
//
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/join")
    public void join(JoinDto joinDto){
        authService.join(joinDto);
    }
}
