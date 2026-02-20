package com.fasthub.backend.user.auth.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.dto.LoginDto;
import com.fasthub.backend.user.usr.dto.LoginResponseDto;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginDto loginDto, HttpServletResponse response) {
        return authRepository.findByUserId(loginDto.getUserId())
                .map(user -> {
                    if (!passwordEncoder.matches(loginDto.getUserPwd(), user.getUserPwd())) {
                        throw new BusinessException(ErrorCode.PWD_NOT_FOUND);
                    }
                    jwtService.generateAccessToken(response, user);
                    jwtService.generateRefreshToken(response, user);
                    return new LoginResponseDto(user);
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.ID_NOT_FOUND));
    }
}
