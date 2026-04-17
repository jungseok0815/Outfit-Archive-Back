package com.fasthub.backend.user.post;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.notification.service.NotificationService;
import com.fasthub.backend.user.post.dto.InsertCommentDto;
import com.fasthub.backend.user.post.dto.UpdateCommentDto;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostComment;
import com.fasthub.backend.user.post.repository.PostCommentRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.post.service.PostCommentService;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostCommentService 테스트")
class PostCommentServiceTest {

    @InjectMocks
    private PostCommentService postCommentService;

    @Mock
    private PostCommentRepository postCommentRepository;

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
        return Post.builder().id(1L).user(user).content("게시글").build();
    }

    private PostComment buildComment(User user, Post post) {
        return PostComment.builder()
                .id(1L)
                .user(user)
                .post(post)
                .content("댓글입니다.")
                .build();
    }

    // ────────────────────────────────────────────────
    // 댓글 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("댓글 등록")
    class Insert {

        @Test
        @DisplayName("성공")
        void insert_success() {
            User postOwner = buildUser(1L);
            User commenter = buildUser(2L);
            Post post = buildPost(postOwner);

            InsertCommentDto dto = mock(InsertCommentDto.class);
            given(dto.getPostId()).willReturn(1L);
            given(dto.getContent()).willReturn("댓글입니다.");

            given(authRepository.findById(2L)).willReturn(Optional.of(commenter));
            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            postCommentService.insert(dto, 2L);

            then(postCommentRepository).should().save(any(PostComment.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void insert_fail_userNotFound() {
            InsertCommentDto dto = mock(InsertCommentDto.class);
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postCommentService.insert(dto, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시글")
        void insert_fail_postNotFound() {
            User commenter = buildUser(1L);
            InsertCommentDto dto = mock(InsertCommentDto.class);
            given(dto.getPostId()).willReturn(999L);

            given(authRepository.findById(1L)).willReturn(Optional.of(commenter));
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postCommentService.insert(dto, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_FAIL_SELECT.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 댓글 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("댓글 수정")
    class Update {

        @Test
        @DisplayName("성공")
        void update_success() {
            User user = buildUser(1L);
            Post post = buildPost(user);
            PostComment comment = buildComment(user, post);

            UpdateCommentDto dto = mock(UpdateCommentDto.class);
            given(dto.getId()).willReturn(1L);
            given(dto.getContent()).willReturn("수정된 댓글");
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(comment));

            postCommentService.update(dto, 1L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 댓글")
        void update_fail_notFound() {
            UpdateCommentDto dto = mock(UpdateCommentDto.class);
            given(dto.getId()).willReturn(999L);
            given(postCommentRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postCommentService.update(dto, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void update_fail_unauthorized() {
            User user = buildUser(1L);
            Post post = buildPost(user);
            PostComment comment = buildComment(user, post);

            UpdateCommentDto dto = mock(UpdateCommentDto.class);
            given(dto.getId()).willReturn(1L);
            given(postCommentRepository.findById(1L)).willReturn(Optional.of(comment));

            assertThatThrownBy(() -> postCommentService.update(dto, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_UNAUTHORIZED.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 댓글 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("댓글 삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            User user = buildUser(1L);
            Post post = buildPost(user);
            PostComment comment = buildComment(user, post);

            given(postCommentRepository.findById(1L)).willReturn(Optional.of(comment));

            postCommentService.delete(1L, 1L);

            then(postCommentRepository).should().delete(comment);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 댓글")
        void delete_fail_notFound() {
            given(postCommentRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postCommentService.delete(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void delete_fail_unauthorized() {
            User user = buildUser(1L);
            Post post = buildPost(user);
            PostComment comment = buildComment(user, post);

            given(postCommentRepository.findById(1L)).willReturn(Optional.of(comment));

            assertThatThrownBy(() -> postCommentService.delete(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COMMENT_UNAUTHORIZED.getMessage());

            then(postCommentRepository).should(never()).delete(any());
        }
    }
}
