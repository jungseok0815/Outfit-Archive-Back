package com.fasthub.backend.oper.auth.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.usr.dto.CustomUserDetails;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import com.fasthub.backend.oper.usr.dto.LoginDto;
import com.fasthub.backend.oper.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Result login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        return authService.login(loginDto, response);
    }


    /**
     * 로그인이 되어잇는지 유저 확인
     * @return
     */
    @GetMapping("/validate")
    public Result vailData(){
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : " + authentication.getName());
        if (!Objects.equals(authentication.getName(), "anonymousUser")) {
            log.info("authentication principal : " + authentication.getPrincipal());
            CustomUserDetails CustomUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return Result.success("인증 성공", CustomUserDetails);
        }
        return Result.fail("인증 실패" , null);
    }
}
