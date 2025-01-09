package com.fasthub.backend.oper.auth.service;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.security.JwtUtil;
import com.fasthub.backend.oper.auth.dto.CustomUserInfoDto;
import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.dto.LoginDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    public String login(LoginDto loginDto){
        User usrEntity = User.builder().userId(loginDto.getUserId())
                .userPw(loginDto.getUserPwd())
                .build();
        Optional<User> user  = authRepository.findByUserId(usrEntity.getUserId());
        if (user.isEmpty()){
            log.info("아이디가 존재하지 않습니다.");
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
        }
        if (!passwordEncoder.matches(loginDto.getUserPwd(), user.get().getUserPw())){
            log.info("password가 존재하지 않습니다.");
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        CustomUserInfoDto info = modelMapper.map(usrEntity, CustomUserInfoDto.class);
        log.info(info.toString());
        String accessToken = jwtUtil.createAccessToken(info);
        log.info("accessToken : " + accessToken);
        return accessToken;
    }

    public void join(JoinDto joinDto){
        User usrEntity = User.builder().userId(joinDto.getUserId())
                .userPw(passwordEncoder.encode(joinDto.getUserPwd()))
                .userAge(joinDto.getUserAge())
                .userNm(joinDto.getUserNm())
                .authName(UserRole.valueOf("ROLE_"+joinDto.getAuthName()))
                .build();
        User resultUsrEntity = authRepository.save(usrEntity);
        log.info("result : " + resultUsrEntity.getAuthorities());
    }


}
