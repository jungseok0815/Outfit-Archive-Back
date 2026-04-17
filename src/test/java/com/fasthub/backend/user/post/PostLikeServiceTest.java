package com.fasthub.backend.user.post;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostLike;
import com.fasthub.backend.user.post.repository.PostLikeRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.post.service.PostLikeService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostLikeService 테스트")
class PostLikeServiceTest {

    @InjectMocks
    private PostLikeService postLikeService;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private NotificationService notificationService;

    private User buildUser(Long id) {
        return User.builder()
                .id(id)
                .userId("user" + id)
                .userNm("사용자" + id)
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    private Post buildPost(User user) {
        return Post.builder()
                .id(1L)
                .user(user)
                .content("테스트 게시글")
                .build();
    }

    // ────────────────────────────────────────────────
    // 좋아요 토글
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("좋아요 토글")
    class Toggle {

        @Test
        @DisplayName("성공 - 좋아요 추가 (true 반환)")
        void toggle_success_add() {
            User postOwner = buildUser(1L);
            User liker = buildUser(2L);
            Post post = buildPost(postOwner);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(authRepository.findById(2L)).willReturn(Optional.of(liker));
            given(postLikeRepository.findByPostIdAndUserId(1L, 2L)).willReturn(Optional.empty());

            boolean result = postLikeService.toggle(1L, 2L);

            assertThat(result).isTrue();
            then(postLikeRepository).should().save(any(PostLike.class));
        }

        @Test
        @DisplayName("성공 - 좋아요 취소 (false 반환)")
        void toggle_success_cancel() {
            User postOwner = buildUser(1L);
            User liker = buildUser(2L);
            Post post = buildPost(postOwner);
            PostLike like = mock(PostLike.class);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(authRepository.findById(2L)).willReturn(Optional.of(liker));
            given(postLikeRepository.findByPostIdAndUserId(1L, 2L)).willReturn(Optional.of(like));

            boolean result = postLikeService.toggle(1L, 2L);

            assertThat(result).isFalse();
            then(postLikeRepository).should().delete(like);
            then(postLikeRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시글")
        void toggle_fail_postNotFound() {
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postLikeService.toggle(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_FAIL_SELECT.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void toggle_fail_userNotFound() {
            User postOwner = buildUser(1L);
            Post post = buildPost(postOwner);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postLikeService.toggle(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 좋아요 수 / 여부
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("좋아요 수 / 여부")
    class CountAndIsLiked {

        @Test
        @DisplayName("성공 - 좋아요 수 조회")
        void count_success() {
            given(postLikeRepository.countByPostId(1L)).willReturn(5L);

            long result = postLikeService.count(1L);

            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("성공 - 좋아요 여부 true")
        void isLiked_true() {
            given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);

            assertThat(postLikeService.isLiked(1L, 1L)).isTrue();
        }

        @Test
        @DisplayName("성공 - 좋아요 여부 false")
        void isLiked_false() {
            given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(false);

            assertThat(postLikeService.isLiked(1L, 1L)).isFalse();
        }
    }
}
