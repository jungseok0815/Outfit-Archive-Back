package com.fasthub.backend.user.follow;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.follow.dto.FollowCountDto;
import com.fasthub.backend.user.follow.dto.FollowUserDto;
import com.fasthub.backend.user.follow.entity.Follow;
import com.fasthub.backend.user.follow.repository.FollowRepository;
import com.fasthub.backend.user.follow.service.FollowService;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService 테스트")
class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private NotificationService notificationService;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser(Long id, String userId, String userNm) {
        return User.builder()
                .id(id)
                .userId(userId)
                .userNm(userNm)
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    // ────────────────────────────────────────────────
    // 팔로우
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("팔로우")
    class Follow {

        @Test
        @DisplayName("성공")
        void follow_success() {
            User follower = buildUser(1L, "user01", "홍길동");
            User following = buildUser(2L, "user02", "김철수");

            given(authRepository.findById(1L)).willReturn(Optional.of(follower));
            given(authRepository.findById(2L)).willReturn(Optional.of(following));
            given(followRepository.existsByFollowerAndFollowing(follower, following)).willReturn(false);

            followService.follow(1L, 2L);

            then(followRepository).should().save(any(com.fasthub.backend.user.follow.entity.Follow.class));
        }

        @Test
        @DisplayName("실패 - 자기 자신 팔로우")
        void follow_fail_selfFollow() {
            assertThatThrownBy(() -> followService.follow(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.FOLLOW_SELF.getMessage());

            then(followRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 팔로워 유저 없음")
        void follow_fail_followerNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> followService.follow(999L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 팔로잉 대상 유저 없음")
        void follow_fail_targetNotFound() {
            User follower = buildUser(1L, "user01", "홍길동");

            given(authRepository.findById(1L)).willReturn(Optional.of(follower));
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> followService.follow(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 이미 팔로우 중")
        void follow_fail_alreadyFollowing() {
            User follower = buildUser(1L, "user01", "홍길동");
            User following = buildUser(2L, "user02", "김철수");

            given(authRepository.findById(1L)).willReturn(Optional.of(follower));
            given(authRepository.findById(2L)).willReturn(Optional.of(following));
            given(followRepository.existsByFollowerAndFollowing(follower, following)).willReturn(true);

            assertThatThrownBy(() -> followService.follow(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.FOLLOW_ALREADY_EXISTS.getMessage());

            then(followRepository).should(never()).save(any());
        }
    }

    // ────────────────────────────────────────────────
    // 언팔로우
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("언팔로우")
    class Unfollow {

        @Test
        @DisplayName("성공")
        void unfollow_success() {
            User follower = buildUser(1L, "user01", "홍길동");
            User following = buildUser(2L, "user02", "김철수");
            com.fasthub.backend.user.follow.entity.Follow follow = com.fasthub.backend.user.follow.entity.Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();

            given(authRepository.findById(1L)).willReturn(Optional.of(follower));
            given(authRepository.findById(2L)).willReturn(Optional.of(following));
            given(followRepository.findByFollowerAndFollowing(follower, following)).willReturn(Optional.of(follow));

            followService.unfollow(1L, 2L);

            then(followRepository).should().delete(follow);
        }

        @Test
        @DisplayName("실패 - 팔로워 유저 없음")
        void unfollow_fail_followerNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> followService.unfollow(999L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 팔로우 관계 없음")
        void unfollow_fail_followNotFound() {
            User follower = buildUser(1L, "user01", "홍길동");
            User following = buildUser(2L, "user02", "김철수");

            given(authRepository.findById(1L)).willReturn(Optional.of(follower));
            given(authRepository.findById(2L)).willReturn(Optional.of(following));
            given(followRepository.findByFollowerAndFollowing(follower, following)).willReturn(Optional.empty());

            assertThatThrownBy(() -> followService.unfollow(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.FOLLOW_NOT_FOUND.getMessage());

            then(followRepository).should(never()).delete(any());
        }
    }

    // ────────────────────────────────────────────────
    // 팔로워 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("팔로워 목록 조회")
    class GetFollowers {

        @Test
        @DisplayName("성공 - 팔로워 있음")
        void getFollowers_success() {
            User follower1 = buildUser(2L, "user02", "김철수");
            User follower2 = buildUser(3L, "user03", "이영희");

            given(followRepository.findFollowersByUserId(1L))
                    .willReturn(List.of(follower1, follower2));

            List<FollowUserDto> result = followService.getFollowers(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getUserNm()).isEqualTo("김철수");
            assertThat(result.get(1).getUserNm()).isEqualTo("이영희");
        }

        @Test
        @DisplayName("성공 - 팔로워 없음")
        void getFollowers_success_empty() {
            given(followRepository.findFollowersByUserId(1L)).willReturn(List.of());

            List<FollowUserDto> result = followService.getFollowers(1L);

            assertThat(result).isEmpty();
        }
    }

    // ────────────────────────────────────────────────
    // 팔로잉 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("팔로잉 목록 조회")
    class GetFollowings {

        @Test
        @DisplayName("성공 - 팔로잉 있음")
        void getFollowings_success() {
            User following1 = buildUser(2L, "user02", "김철수");
            User following2 = buildUser(3L, "user03", "이영희");

            given(followRepository.findFollowingsByUserId(1L))
                    .willReturn(List.of(following1, following2));

            List<FollowUserDto> result = followService.getFollowings(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getUserNm()).isEqualTo("김철수");
        }

        @Test
        @DisplayName("성공 - 팔로잉 없음")
        void getFollowings_success_empty() {
            given(followRepository.findFollowingsByUserId(1L)).willReturn(List.of());

            List<FollowUserDto> result = followService.getFollowings(1L);

            assertThat(result).isEmpty();
        }
    }

    // ────────────────────────────────────────────────
    // 팔로우 여부 확인
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("팔로우 여부 확인")
    class IsFollowing {

        @Test
        @DisplayName("팔로우 중 → true")
        void isFollowing_true() {
            given(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).willReturn(true);

            boolean result = followService.isFollowing(1L, 2L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("팔로우 안 함 → false")
        void isFollowing_false() {
            given(followRepository.existsByFollowerIdAndFollowingId(1L, 2L)).willReturn(false);

            boolean result = followService.isFollowing(1L, 2L);

            assertThat(result).isFalse();
        }
    }

    // ────────────────────────────────────────────────
    // 팔로우 수 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("팔로우 수 조회")
    class GetFollowCount {

        @Test
        @DisplayName("성공")
        void getFollowCount_success() {
            given(followRepository.countByFollowingId(1L)).willReturn(10L);
            given(followRepository.countByFollowerId(1L)).willReturn(5L);

            FollowCountDto result = followService.getFollowCount(1L);

            assertThat(result.getFollowerCount()).isEqualTo(10L);
            assertThat(result.getFollowingCount()).isEqualTo(5L);
        }
    }
}
