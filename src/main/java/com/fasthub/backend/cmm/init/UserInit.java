package com.fasthub.backend.cmm.init;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class UserInit implements ApplicationRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (authRepository.count() > 0) {
            return;
        }

        authRepository.save(User.builder()
                .userId("user1")
                .userPwd(passwordEncoder.encode("user1234"))
                .userNm("홍길동")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build());

        authRepository.save(User.builder()
                .userId("user2")
                .userPwd(passwordEncoder.encode("user1234"))
                .userNm("김영희")
                .userAge(30)
                .authName(UserRole.ROLE_USER)
                .build());

        authRepository.save(User.builder()
                .userId("user3")
                .userPwd(passwordEncoder.encode("user1234"))
                .userNm("이철수")
                .userAge(22)
                .authName(UserRole.ROLE_USER)
                .build());

        log.info("[Init] User 초기 데이터 생성 완료 - 3건");
    }
}
