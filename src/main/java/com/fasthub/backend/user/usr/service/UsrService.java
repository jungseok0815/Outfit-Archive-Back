package com.fasthub.backend.user.usr.service;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.dto.UserDto;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.mapper.AuthMapper;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsrService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto insert(JoinDto joinDto) {
        log.info("joinDto : " + joinDto);
        joinDto.setUserPwd(passwordEncoder.encode(joinDto.getUserPwd()));
        joinDto.setAuthName(UserRole.ROLE_USER.getRole(joinDto.getAuthName()));

        User userEntity = authMapper.userDtoToUserEntity(joinDto);
        return authMapper.userEntityToUserDto(authRepository.save(userEntity));
    }
}
