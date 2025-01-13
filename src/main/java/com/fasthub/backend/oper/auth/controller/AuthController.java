package com.fasthub.backend.oper.auth.controller;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.jwt.JwtGenerator;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.auth.dto.CustomUserDetails;
import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.LoginDto;
import com.fasthub.backend.oper.auth.dto.UserDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.oper.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        authService.login(loginDto, response);
        return ResponseEntity.status(HttpStatus.OK).body("test");
    }

    @PostMapping("/join")
    public Result join(JoinDto joinDto){
        User user = authService.join(joinDto);
        if (user == null){
            return Result.fail("회원가입 실패");
        }else{
           return Result.success("회원가입 성공");
        }
    }

    /**
     * 로그인이 되어잇는지 유저 확인
     * @return
     */
    @GetMapping("/validate")
    public Result vailData(){
        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getName();
        if (authentication.getName() != "anonymousUser" ) {
            CustomUserDetails CustomUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return Result.success("인증 성공", CustomUserDetails);
        }
        return Result.fail("인증 실패" , null);

    }

}
