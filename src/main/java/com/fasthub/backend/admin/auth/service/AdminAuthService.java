package com.fasthub.backend.admin.auth.service;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginResponseDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.fasthub.backend.cmm.error.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthService {

    private final PasswordEncoder passwordEncoder;
    private final AdminMemberRepository adminMemberRepository;

    public AdminLoginResponseDto adminLogin(AdminLoginDto adminLoginDto) {
        AdminMember adminMember = adminMemberRepository.findByMemberId(adminLoginDto.getMemberId())
                .orElseThrow(() -> new BusinessException(ADMIN_ID_NOT_FOUND));

        if (!passwordEncoder.matches(adminLoginDto.getMemberPwd(), adminMember.getMemberPwd())) {
            throw new BusinessException(ADMIN_PWD_NOT_MATCH);
        }

        log.info("admin login success : {}", adminMember.getMemberId());
        return new AdminLoginResponseDto(adminMember);
    }

    public void adminJoin(AdminJoinDto adminJoinDto) {
        if (adminMemberRepository.existsByMemberId(adminJoinDto.getMemberId())) {
            throw new BusinessException(ADMIN_ALREADY_EXISTS);
        }

        AdminMember adminMember = AdminMember.builder()
                .memberId(adminJoinDto.getMemberId())
                .memberPwd(passwordEncoder.encode(adminJoinDto.getMemberPwd()))
                .memberNm(adminJoinDto.getMemberNm())
                .authName(UserRole.ROLE_ADMIN)
                .adminRole(adminJoinDto.getAdminRole())
                .build();

        adminMemberRepository.save(adminMember);
        log.info("admin join success : {}", adminMember.getMemberId());
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
