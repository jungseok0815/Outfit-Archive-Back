package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.cmm.enums.AdminRole;
import com.fasthub.backend.cmm.enums.UserRole;
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
@Order(1)
public class AdminMemberInit implements ApplicationRunner {

    private final AdminMemberRepository adminMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (adminMemberRepository.count() > 0) {
            return;
        }

        adminMemberRepository.save(AdminMember.builder()
                .memberId("wjdtjr9401@naver.com")
                .memberPwd(passwordEncoder.encode("kil79518@"))
                .memberNm("최고관리자")
                .authName(UserRole.ROLE_ADMIN)
                .adminRole(AdminRole.SUPER_ADMIN)
                .build());

        adminMemberRepository.save(AdminMember.builder()
                .memberId("admin1")
                .memberPwd(passwordEncoder.encode("admin1234"))
                .memberNm("일반관리자")
                .authName(UserRole.ROLE_ADMIN)
                .adminRole(AdminRole.ADMIN)
                .build());

        adminMemberRepository.save(AdminMember.builder()
                .memberId("partner1")
                .memberPwd(passwordEncoder.encode("admin1234"))
                .memberNm("협력업체A")
                .authName(UserRole.ROLE_ADMIN)
                .adminRole(AdminRole.PARTNER)
                .build());

        log.info("[Init] AdminMember 초기 데이터 생성 완료 - 3건");
    }
}
