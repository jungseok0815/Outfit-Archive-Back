package com.fasthub.backend.oper.auth.service;

import com.fasthub.backend.oper.auth.dto.CustomUserDetails;
import com.fasthub.backend.oper.auth.dto.UserDto;
import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.auth.repository.AuthRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class CoustomUserDetailService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = authRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

       log.info("customUserDetailArea : " + user.toString());

        UserDto dto = modelMapper.map(user, UserDto.class);

        return new CustomUserDetails(dto);

    }
}
