package com.fasthub.backend.oper.auth.service;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.auth.dto.UserDto;
import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.LoginDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.mapper.AuthMapper;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public Result login(LoginDto loginDto, HttpServletResponse response){
        authRepository.findByUserId(loginDto.getUserId()).ifPresentOrElse(user -> {
            if (!passwordEncoder.matches(loginDto.getUserPwd(), user.getUserPw())) {
                throw new BusinessException(ErrorCode.PWD_NOT_FOUND);
            }
            jwtService.generateAccessToken(response, user);
            jwtService.generateRefreshToken(response, user);
        },
        () -> new BusinessException(ErrorCode.ID_NOT_FOUND));
        return Result.success("login");
    }

    public Result join(JoinDto joinDto){
        User userEntity = authMapper.userDtoToUserEntity(joinDto);
        return Result.success("join",authMapper.userEntityToUserDto(authRepository.save(userEntity)));
    }


}
