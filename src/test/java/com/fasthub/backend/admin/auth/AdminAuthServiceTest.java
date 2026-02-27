package com.fasthub.backend.admin.auth;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginResponseDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import com.fasthub.backend.admin.auth.service.AdminAuthService;
import com.fasthub.backend.cmm.enums.AdminRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminAuthService 테스트")
class AdminAuthServiceTest {

    @InjectMocks
    private AdminAuthService adminAuthService;

    @Mock
    private AdminMemberRepository adminMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse response;

    private AdminMember buildMember() {
        return AdminMember.builder()
                .memberId("admin01")
                .memberPwd("encodedPassword")
                .memberNm("관리자")
                .adminRole(AdminRole.ADMIN)
                .build();
    }

    // ────────────────────────────────────────────────
    // 로그인
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공")
        void login_success() {
            AdminLoginDto dto = new AdminLoginDto("admin01", "rawPassword");
            AdminMember member = buildMember();

            given(adminMemberRepository.findByMemberId("admin01")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("rawPassword", "encodedPassword")).willReturn(true);

            AdminLoginResponseDto result = adminAuthService.adminLogin(dto, response);

            assertThat(result.getMemberId()).isEqualTo("admin01");
            assertThat(result.getMemberNm()).isEqualTo("관리자");
            assertThat(result.getAdminRole()).isEqualTo(AdminRole.ADMIN);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 아이디")
        void login_fail_memberIdNotFound() {
            AdminLoginDto dto = new AdminLoginDto("notExist", "rawPassword");

            given(adminMemberRepository.findByMemberId("notExist")).willReturn(Optional.empty());

            assertThatThrownBy(() -> adminAuthService.adminLogin(dto, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ADMIN_ID_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void login_fail_passwordNotMatch() {
            AdminLoginDto dto = new AdminLoginDto("admin01", "wrongPassword");
            AdminMember member = buildMember();

            given(adminMemberRepository.findByMemberId("admin01")).willReturn(Optional.of(member));
            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            assertThatThrownBy(() -> adminAuthService.adminLogin(dto, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ADMIN_PWD_NOT_MATCH.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 회원가입
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("회원가입")
    class Join {

        @Test
        @DisplayName("성공")
        void join_success() {
            AdminJoinDto dto = new AdminJoinDto("admin01", "password1", "관리자", AdminRole.ADMIN);

            given(adminMemberRepository.existsByMemberId("admin01")).willReturn(false);
            given(passwordEncoder.encode("password1")).willReturn("encodedPassword");

            adminAuthService.adminJoin(dto);

            then(adminMemberRepository).should().save(any(AdminMember.class));
        }

        @Test
        @DisplayName("실패 - 중복 아이디")
        void join_fail_duplicateMemberId() {
            AdminJoinDto dto = new AdminJoinDto("admin01", "password1", "관리자", AdminRole.ADMIN);

            given(adminMemberRepository.existsByMemberId("admin01")).willReturn(true);

            assertThatThrownBy(() -> adminAuthService.adminJoin(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ADMIN_ALREADY_EXISTS.getMessage());

            then(adminMemberRepository).should(never()).save(any());
        }
    }
}
