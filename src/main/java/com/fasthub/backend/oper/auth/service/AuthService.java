package com.fasthub.backend.oper.auth.service;

import com.fasthub.backend.cmm.enums.ErrorCode;
import com.fasthub.backend.cmm.enums.JwtRule;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.exception.BusinessException;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.oper.auth.dto.UserDto;
import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.LoginDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User login(LoginDto loginDto, HttpServletResponse response){
        User usrEntity = User.builder().userId(loginDto.getUserId())
                .userPw(loginDto.getUserPwd())
                .build();
        Optional<User> user  = authRepository.findByUserId(usrEntity.getUserId());

        if (user.isEmpty()){
            throw new BusinessException(ErrorCode.ID_NOT_FOUND);
        }
        if (!passwordEncoder.matches(loginDto.getUserPwd(), user.get().getUserPw())){
            throw new BusinessException(ErrorCode.PWD_NOT_FOUND);
        }
        jwtService.generateAccessToken(response, user.get());
        jwtService.generateRefreshToken(response, user.get());
        return user.get();
    }

    public User join(JoinDto joinDto){
        User usrEntity = User.builder().userId(joinDto.getUserId())
                .userPw(passwordEncoder.encode(joinDto.getUserPwd()))
                .userAge(joinDto.getUserAge())
                .userNm(joinDto.getUserNm())
                .authName(UserRole.ADMIN)
                .build();
        return authRepository.save(usrEntity);
    }
}
