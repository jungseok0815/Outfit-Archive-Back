package com.fasthub.backend.admin.auth.service;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginResponseDto;
import com.fasthub.backend.admin.auth.dto.AdminMemberResponseDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.cmm.enums.AdminRole;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fasthub.backend.cmm.error.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

    private final PasswordEncoder passwordEncoder;
    private final AdminMemberRepository adminMemberRepository;
    private final JwtService jwtService;

    public AdminLoginResponseDto adminLogin(AdminLoginDto adminLoginDto, HttpServletResponse response) {
        AdminMember adminMember = adminMemberRepository.findByMemberId(adminLoginDto.getMemberId())
                .orElseThrow(() -> new BusinessException(ADMIN_ID_NOT_FOUND));

        if (!passwordEncoder.matches(adminLoginDto.getMemberPwd(), adminMember.getMemberPwd())) {
            throw new BusinessException(ADMIN_PWD_NOT_MATCH);
        }
        jwtService.generateAccessToken(response, adminMember);
        jwtService.generateRefreshToken(response, adminMember);

        log.info("admin login success : {}", adminMember.getMemberId());
        return new AdminLoginResponseDto(adminMember);
    }

    public void adminJoin(AdminJoinDto adminJoinDto) {
        if (adminMemberRepository.existsByMemberId(adminJoinDto.getMemberId())) {
            throw new BusinessException(ADMIN_ALREADY_EXISTS);
        }

        String affiliation = adminJoinDto.getAffiliation();
        if (affiliation == null || affiliation.isBlank()) {
            if (adminJoinDto.getAdminRole() == AdminRole.SUPER_ADMIN || adminJoinDto.getAdminRole() == AdminRole.ADMIN) {
                affiliation = "OA";
            }
        }

        AdminMember adminMember = AdminMember.builder()
                .memberId(adminJoinDto.getMemberId())
                .memberPwd(passwordEncoder.encode(adminJoinDto.getMemberPwd()))
                .memberNm(adminJoinDto.getMemberNm())
                .affiliation(affiliation)
                .adminRole(adminJoinDto.getAdminRole())
                .build();

        adminMemberRepository.save(adminMember);
        log.info("admin join success : {}", adminMember.getMemberId());
    }

    public List<AdminMemberResponseDto> getAdminList() {
        return adminMemberRepository.findAll().stream()
                .map(AdminMemberResponseDto::new)
                .toList();
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
