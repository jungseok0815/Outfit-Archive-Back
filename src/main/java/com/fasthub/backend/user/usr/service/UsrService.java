package com.fasthub.backend.user.usr.service;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.result.Params;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.user.usr.dto.JoinDto;
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

    public Result insert(JoinDto joinDto){
        log.info("joinDto : " + joinDto);
        System.out.println("jooinDto : " + joinDto.toString());
        joinDto.setUserPwd(passwordEncoder.encode(joinDto.getUserPwd()));
        joinDto.setAuthName(UserRole.ROLE_USER.getRole(joinDto.getAuthName()));

        User userEntity = authMapper.userDtoToUserEntity(joinDto);
        return Result.success("join",authMapper.userEntityToUserDto(authRepository.save(userEntity)));
    }

    public Result insert(Params params){
        log.info("params : " + params.toString());
        return Result.success("join");
    }
}
