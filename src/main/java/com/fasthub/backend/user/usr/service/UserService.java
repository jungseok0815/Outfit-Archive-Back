package com.fasthub.backend.user.usr.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.usr.dto.*;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.mapper.AuthMapper;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
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

    @Transactional
    public UserDto join(JoinDto joinDto) {
        if (authRepository.existsByUserId(joinDto.getUserId())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
        log.info("joinDto : " + joinDto);
        joinDto.setUserPwd(passwordEncoder.encode(joinDto.getUserPwd()));
        joinDto.setAuthName(UserRole.ROLE_USER.getRole(joinDto.getAuthName()));
        User userEntity = authMapper.userDtoToUserEntity(joinDto);
        return authMapper.userEntityToUserDto(authRepository.save(userEntity));
    }

    // 유저 목록 조회 (이름으로 키워드 검색, 페이징)
    public Page<ResponseUserDto> list(String keyword, Pageable pageable) {
        return authRepository.findAllByKeyword(keyword, pageable)
                .map(ResponseUserDto::new);
    }

    // 유저 정보 수정 (이름, 나이, 비밀번호)
    @Transactional
    public void update(UpdateUserDto dto) {
        User user = authRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.update(dto.getUserNm(), dto.getUserAge(), passwordEncoder.encode(dto.getUserPwd()));
    }

    // 유저 삭제
    @Transactional
    public void delete(Long id) {
        User user = authRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        authRepository.delete(user);
    }
}
