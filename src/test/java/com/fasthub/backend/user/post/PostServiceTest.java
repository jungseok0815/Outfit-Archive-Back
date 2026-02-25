package com.fasthub.backend.user.post;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.user.post.dto.InsertPostDto;
import com.fasthub.backend.user.post.dto.ResponsePostDto;
import com.fasthub.backend.user.post.dto.UpdatePostDto;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostImg;
import com.fasthub.backend.user.post.repository.PostImgRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.post.service.PostService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService 테스트")
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostImgRepository postImgRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ImgHandler imgHandler;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser() {
        return User.builder()
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .build();
    }

    private Post buildPost(User user) {
        return Post.builder()
                .title("오늘의 코디")
                .content("봄 코디 공유합니다.")
                .user(user)
                .build();
    }

    // ────────────────────────────────────────────────
    // 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("목록 조회")
    class PostList {

        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회")
        void list_success_noKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser();
            Post post = buildPost(user);
            Page<Post> postPage = new PageImpl<>(List.of(post));

            given(postRepository.findAllByKeyword(null, pageable)).willReturn(postPage);

            Page<ResponsePostDto> result = postService.list(null, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("오늘의 코디");
        }

        @Test
        @DisplayName("성공 - 키워드로 검색")
        void list_success_withKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            String keyword = "코디";
            User user = buildUser();
            Post post = buildPost(user);
            Page<Post> postPage = new PageImpl<>(List.of(post));

            given(postRepository.findAllByKeyword(keyword, pageable)).willReturn(postPage);

            Page<ResponsePostDto> result = postService.list(keyword, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 결과 없음")
        void list_success_empty() {
            Pageable pageable = PageRequest.of(0, 10);

            given(postRepository.findAllByKeyword("없는키워드", pageable))
                    .willReturn(new PageImpl<>(List.of()));

            Page<ResponsePostDto> result = postService.list("없는키워드", pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    // ────────────────────────────────────────────────
    // 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("등록")
    class Insert {

        @Test
        @DisplayName("성공 - 이미지 없이 등록")
        void insert_success_withoutImage() {
            InsertPostDto dto = new InsertPostDto();
            dto.setTitle("오늘의 코디");
            dto.setContent("봄 코디 공유합니다.");
            // images = null

            User user = buildUser();
            Post post = buildPost(user);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(postRepository.save(any(Post.class))).willReturn(post);

            postService.insert(dto, 1L);

            then(postRepository).should().save(any(Post.class));
            then(postImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 포함 등록")
        void insert_success_withImage() {
            MultipartFile mockFile1 = mock(MultipartFile.class);
            MultipartFile mockFile2 = mock(MultipartFile.class);

            InsertPostDto dto = new InsertPostDto();
            dto.setTitle("오늘의 코디");
            dto.setContent("봄 코디 공유합니다.");
            dto.setImages(List.of(mockFile1, mockFile2));

            User user = buildUser();
            Post post = buildPost(user);
            PostImg postImg1 = new PostImg();
            PostImg postImg2 = new PostImg();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(postRepository.save(any(Post.class))).willReturn(post);
            given(imgHandler.createImg(eq(mockFile1), any(), eq(post))).willReturn(postImg1);
            given(imgHandler.createImg(eq(mockFile2), any(), eq(post))).willReturn(postImg2);

            postService.insert(dto, 1L);

            // 이미지 2개 저장 검증
            then(postImgRepository).should(times(2)).save(any(PostImg.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void insert_fail_userNotFound() {
            InsertPostDto dto = new InsertPostDto();
            dto.setTitle("오늘의 코디");
            dto.setContent("봄 코디 공유합니다.");

            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.insert(dto, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

            then(postRepository).should(never()).save(any());
        }
    }

    // ────────────────────────────────────────────────
    // 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("수정")
    class Update {

        @Test
        @DisplayName("성공 - 이미지 없이 수정")
        void update_success_withoutImage() {
            UpdatePostDto dto = new UpdatePostDto();
            dto.setId(1L);
            dto.setTitle("수정된 코디");
            dto.setContent("여름 코디로 변경합니다.");
            // images = null

            User user = buildUser();
            Post post = buildPost(user);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            postService.update(dto);

            // 제목, 내용이 수정됐는지 검증
            assertThat(post.getTitle()).isEqualTo("수정된 코디");
            assertThat(post.getContent()).isEqualTo("여름 코디로 변경합니다.");
            // 이미지 없으면 삭제·저장 안 됨
            then(postImgRepository).should(never()).deleteByPost(any());
            then(postImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 교체 수정")
        void update_success_withImage() {
            MultipartFile mockFile = mock(MultipartFile.class);

            UpdatePostDto dto = new UpdatePostDto();
            dto.setId(1L);
            dto.setTitle("수정된 코디");
            dto.setContent("여름 코디로 변경합니다.");
            dto.setImages(List.of(mockFile));

            User user = buildUser();
            Post post = buildPost(user);
            PostImg newPostImg = new PostImg();

            given(postRepository.findById(1L)).willReturn(Optional.of(post));
            given(imgHandler.createImg(eq(mockFile), any(), eq(post))).willReturn(newPostImg);

            postService.update(dto);

            // 기존 이미지 삭제 후 새 이미지 저장
            then(postImgRepository).should().deleteByPost(post);
            then(postImgRepository).should().save(newPostImg);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시글")
        void update_fail_postNotFound() {
            UpdatePostDto dto = new UpdatePostDto();
            dto.setId(999L);
            dto.setTitle("수정된 코디");
            dto.setContent("여름 코디로 변경합니다.");

            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.update(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_FAIL_SELECT.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            User user = buildUser();
            Post post = buildPost(user);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            postService.delete(1L);

            then(postRepository).should().delete(post);
        }

        @Test
        @DisplayName("성공 - 이미지 포함 게시글 삭제 (CascadeType.ALL 동작)")
        void delete_success_withImages() {
            User user = buildUser();
            Post post = buildPost(user);

            given(postRepository.findById(1L)).willReturn(Optional.of(post));

            postService.delete(1L);

            // postRepository.delete() 호출 시 CascadeType.ALL로 PostImg도 자동 삭제
            then(postRepository).should().delete(post);
            // postImgRepository를 직접 호출할 필요 없음
            then(postImgRepository).should(never()).deleteByPost(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 게시글")
        void delete_fail_postNotFound() {
            given(postRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> postService.delete(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BOARD_FAIL_SELECT.getMessage());

            then(postRepository).should(never()).delete(any());
        }
    }
}
