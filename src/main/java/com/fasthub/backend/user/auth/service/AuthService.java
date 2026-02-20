package com.fasthub.backend.user.auth.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.user.usr.dto.LoginDto;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Result login(LoginDto loginDto, HttpServletResponse response){
        AtomicReference<User> resData = new AtomicReference<>();
        authRepository.findByUserId(loginDto.getUserId()).ifPresentOrElse(user -> {
            if (!passwordEncoder.matches(loginDto.getUserPwd(), user.getUserPwd())) {;
                throw new BusinessException(ErrorCode.PWD_NOT_FOUND);
            }
            jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);
            resData.set(user);
        }, () -> new BusinessException(ErrorCode.ID_NOT_FOUND));

        //resData
        return Result.success("login", resData.get());
    }

}
