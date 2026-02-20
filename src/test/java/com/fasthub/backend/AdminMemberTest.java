package com.fasthub.backend;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.admin.auth.service.AdminAuthService;
import com.fasthub.backend.cmm.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class AdminMemberTest {

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private AdminMemberRepository adminMemberRepository;

    @Test
    @DisplayName("관리자 회원가입 성공")
    void adminJoinTest(){
        AdminJoinDto adminJoinDto = new AdminJoinDto();
        adminJoinDto.setMemberId("wjdtjr9401@naver.com");
        adminJoinDto.setMemberPwd("wjdtjr9401");
        adminJoinDto.setMemberNm("차정석");

        adminAuthService.adminJoin(adminJoinDto);

        Optional<AdminMember> saved = adminMemberRepository.findByMemberId(adminJoinDto.getMemberId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getMemberNm()).isEqualTo(adminJoinDto.getMemberNm());
        assertThat(saved.get().getAuthName()).isEqualTo(UserRole.ROLE_ADMIN);
        // 비밀번호는 암호화되어 저장되므로 평문과 달라야 함
        assertThat(saved.get().getMemberPwd()).isNotEqualTo("wjdtjr9401");
        // 암호화된 비밀번호가 원본과 일치하는지 검증
        assertThat(adminAuthService.matchesPassword("wjdtjr9401", saved.get().getMemberPwd())).isTrue();
    }

    @Test
    @DisplayName("중복 아이디로 회원가입 시 예외 발생")
    void adminJoinDuplicateTest(){
        AdminJoinDto adminJoinDto = new AdminJoinDto();
        adminJoinDto.setMemberId("wjdtjr9401@naver.com");
        adminJoinDto.setMemberPwd("wjdtjr9401");
        adminJoinDto.setMemberNm("차정석");

        assertThatThrownBy(() -> adminAuthService.adminJoin(adminJoinDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 아이디입니다");
    }
}
