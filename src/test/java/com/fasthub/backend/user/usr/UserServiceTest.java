package com.fasthub.backend.user.usr;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.jwt.JwtService;
import com.fasthub.backend.user.follow.repository.FollowRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.usr.dto.*;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.mapper.AuthMapper;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import com.fasthub.backend.user.usr.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private ImgHandler imgHandler;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HttpServletResponse response;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser() {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
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
            LoginDto dto = new LoginDto("user01", "rawPwd");
            User user = buildUser();

            given(authRepository.findByUserId("user01")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("rawPwd", "encodedPwd")).willReturn(true);

            LoginResponseDto result = userService.login(dto, response);

            assertThat(result.getUserId()).isEqualTo("user01");
            then(jwtService).should().generateAccessToken(response, user);
            then(jwtService).should().generateRefreshToken(response, user);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 아이디")
        void login_fail_idNotFound() {
            LoginDto dto = new LoginDto("notExist", "rawPwd");

            given(authRepository.findByUserId("notExist")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.login(dto, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ID_NOT_FOUND.getMessage());

            then(jwtService).should(never()).generateAccessToken(any(), any());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void login_fail_passwordNotMatch() {
            LoginDto dto = new LoginDto("user01", "wrongPwd");
            User user = buildUser();

            given(authRepository.findByUserId("user01")).willReturn(Optional.of(user));
            given(passwordEncoder.matches("wrongPwd", "encodedPwd")).willReturn(false);

            assertThatThrownBy(() -> userService.login(dto, response))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PWD_NOT_FOUND.getMessage());

            then(jwtService).should(never()).generateAccessToken(any(), any());
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
            JoinDto dto = new JoinDto("user01", "rawPwd", "홍길동", 25, "USER");
            User user = buildUser();
            UserDto userDto = new UserDto(user);

            given(authRepository.existsByUserId("user01")).willReturn(false);
            given(passwordEncoder.encode("rawPwd")).willReturn("encodedPwd");
            given(authMapper.userDtoToUserEntity(any(JoinDto.class))).willReturn(user);
            given(authRepository.save(user)).willReturn(user);
            given(authMapper.userEntityToUserDto(user)).willReturn(userDto);

            UserDto result = userService.join(dto);

            assertThat(result.getUserId()).isEqualTo("user01");
            then(authRepository).should().save(user);
        }

        @Test
        @DisplayName("실패 - 중복 아이디")
        void join_fail_duplicateId() {
            JoinDto dto = new JoinDto("user01", "rawPwd", "홍길동", 25, "USER");

            given(authRepository.existsByUserId("user01")).willReturn(true);

            assertThatThrownBy(() -> userService.join(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_ALREADY_EXISTS.getMessage());

            then(authRepository).should(never()).save(any());
        }
    }

    // ────────────────────────────────────────────────
    // 프로필 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("프로필 조회")
    class GetProfile {

        @Test
        @DisplayName("성공")
        void getProfile_success() {
            User user = buildUser();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(followRepository.countByFollowingId(1L)).willReturn(5L);
            given(followRepository.countByFollowerId(1L)).willReturn(3L);
            given(postRepository.countByUser_Id(1L)).willReturn(10L);

            ProfileDto result = userService.getProfile(1L);

            assertThat(result.getUserId()).isEqualTo("user01");
            assertThat(result.getFollowerCount()).isEqualTo(5L);
            assertThat(result.getFollowingCount()).isEqualTo(3L);
            assertThat(result.getPostCount()).isEqualTo(10L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void getProfile_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getProfile(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 유저 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("유저 목록 조회")
    class List {

        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회")
        void list_success_noKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser();
            Page<User> userPage = new PageImpl<>(java.util.List.of(user));

            given(authRepository.findAllByKeyword(null, pageable)).willReturn(userPage);

            Page<ResponseUserDto> result = userService.list(null, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUserId()).isEqualTo("user01");
        }

        @Test
        @DisplayName("성공 - 키워드 검색")
        void list_success_withKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser();
            Page<User> userPage = new PageImpl<>(java.util.List.of(user));

            given(authRepository.findAllByKeyword("홍", pageable)).willReturn(userPage);

            Page<ResponseUserDto> result = userService.list("홍", pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 결과 없음")
        void list_success_empty() {
            Pageable pageable = PageRequest.of(0, 10);

            given(authRepository.findAllByKeyword("없는이름", pageable))
                    .willReturn(new PageImpl<>(java.util.List.of()));

            Page<ResponseUserDto> result = userService.list("없는이름", pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    // ────────────────────────────────────────────────
    // 유저 정보 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("유저 정보 수정")
    class Update {

        @Test
        @DisplayName("성공")
        void update_success() {
            UpdateUserDto dto = new UpdateUserDto(1L, "김철수", 30, "newPwd123!", "소개글");
            User user = buildUser();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(passwordEncoder.encode("newPwd123!")).willReturn("encodedNewPwd");

            userService.update(dto);

            assertThat(user.getUserNm()).isEqualTo("김철수");
            assertThat(user.getUserAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void update_fail_userNotFound() {
            UpdateUserDto dto = new UpdateUserDto(999L, "김철수", 30, "newPwd123!", "소개글");

            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 프로필 이미지 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("프로필 이미지 수정")
    class UpdateProfileImg {

        @Test
        @DisplayName("성공 - 기존 이미지 없음")
        void updateProfileImg_success_noExistingImg() {
            User user = buildUser(); // profileImgNm = null
            MultipartFile mockFile = mock(MultipartFile.class);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(mockFile.getOriginalFilename()).willReturn("new.jpg");
            given(imgHandler.getFileName("new.jpg")).willReturn("uuid-new.jpg");
            given(imgHandler.upload(mockFile, "uuid-new.jpg")).willReturn("https://s3.../uuid-new.jpg");

            String result = userService.updateProfileImg(1L, mockFile);

            assertThat(result).isEqualTo("https://s3.../uuid-new.jpg");
            then(imgHandler).should(never()).deleteFile(anyString());
        }

        @Test
        @DisplayName("성공 - 기존 이미지 있음 → S3 삭제 후 업로드")
        void updateProfileImg_success_withExistingImg() {
            User user = User.builder()
                    .id(1L)
                    .userId("user01")
                    .userNm("홍길동")
                    .userPwd("encodedPwd")
                    .userAge(25)
                    .authName(UserRole.ROLE_USER)
                    .profileImgNm("https://s3.../old.jpg")
                    .build();
            MultipartFile mockFile = mock(MultipartFile.class);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(mockFile.getOriginalFilename()).willReturn("new.jpg");
            given(imgHandler.getFileName("new.jpg")).willReturn("uuid-new.jpg");
            given(imgHandler.upload(mockFile, "uuid-new.jpg")).willReturn("https://s3.../uuid-new.jpg");

            String result = userService.updateProfileImg(1L, mockFile);

            assertThat(result).isEqualTo("https://s3.../uuid-new.jpg");
            then(imgHandler).should().deleteFile("https://s3.../old.jpg");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void updateProfileImg_fail_userNotFound() {
            MultipartFile mockFile = mock(MultipartFile.class);

            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfileImg(999L, mockFile))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 유저 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("유저 삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            User user = buildUser();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));

            userService.delete(1L);

            then(authRepository).should().delete(user);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void delete_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

            then(authRepository).should(never()).delete(any());
        }
    }
}
