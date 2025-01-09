package com.fasthub.backend.cmm.security;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.oper.auth.dto.JoinDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId).orElseThrow(()-> new IllegalArgumentException((userId)));
    }

    public void join(JoinDto joinDto){
        User usrEntity = User.builder().userId(joinDto.getUserId())
                .userPw(passwordEncoder.encode(joinDto.getUserPwd()))
                .userAge(joinDto.getUserAge())
                .userNm(joinDto.getUserNm())
                .authName(UserRole.valueOf("ROLE_"+joinDto.getAuthName()))
                .build();
        User resultUsrEntity = userRepository.save(usrEntity);
        log.info("result : " + resultUsrEntity.getAuthorities());
    }
}
