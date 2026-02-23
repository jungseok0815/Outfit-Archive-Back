package com.fasthub.backend.user.usr.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.dto.LoginDto;
import com.fasthub.backend.user.usr.dto.LoginResponseDto;
import com.fasthub.backend.user.usr.dto.UserDto;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.mapper.AuthMapper;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
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

    public UserDto join(JoinDto joinDto) {
        log.info("joinDto : " + joinDto);
        joinDto.setUserPwd(passwordEncoder.encode(joinDto.getUserPwd()));
        joinDto.setAuthName(UserRole.ROLE_USER.getRole(joinDto.getAuthName()));
        User userEntity = authMapper.userDtoToUserEntity(joinDto);
        return authMapper.userEntityToUserDto(authRepository.save(userEntity));
    }
}
