package com.fasthub.backend.admin.auth.service;

import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.cmm.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthService {

    private final PasswordEncoder passwordEncoder;
    private final AdminMemberRepository adminMemberRepository;

    public void adminLogin(AdminLoginDto adminLoginDto){
        log.info("admin Info : {}",  adminLoginDto.toString());
    }

    public void adminJoin(AdminJoinDto adminJoinDto){
        log.info("join dto Info : {}",  adminJoinDto.toString());

        if (adminMemberRepository.existsByMemberId(adminJoinDto.getMemberId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다: " + adminJoinDto.getMemberId());
        }

        AdminMember adminMember = AdminMember.builder()
                .memberId(adminJoinDto.getMemberId())
                .memberPwd(encodePassword(adminJoinDto.getMemberPwd()))
                .memberNm(adminJoinDto.getMemberNm())
                .authName(UserRole.ROLE_ADMIN)
                .adminRole(adminJoinDto.getAdminRole())
                .build();

        adminMemberRepository.save(adminMember);
        log.info("admin join success : {}", adminMember.getMemberId());
    }

    /**
     * DTO의 비밀번호를 BCrypt로 암호화하여 반환
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 입력된 비밀번호와 암호화된 비밀번호 일치 여부 검증
     * BCrypt는 단방향 해시이므로 복호화 대신 matches로 검증
     */
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
