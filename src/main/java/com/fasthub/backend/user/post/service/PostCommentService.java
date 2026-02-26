package com.fasthub.backend.user.post.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.post.dto.InsertCommentDto;
import com.fasthub.backend.user.post.dto.ResponseCommentDto;
import com.fasthub.backend.user.post.dto.UpdateCommentDto;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostComment;
import com.fasthub.backend.user.post.repository.PostCommentRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    // 댓글 목록 조회
    public Page<ResponseCommentDto> list(Long postId, Pageable pageable) {
        return postCommentRepository.findByPostId(postId, pageable)
                .map(ResponseCommentDto::new);
    }

    // 댓글 등록
    @Transactional
    public void insert(InsertCommentDto dto, Long userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_FAIL_SELECT));

        postCommentRepository.save(PostComment.builder()
                .content(dto.getContent())
                .user(user)
                .post(post)
                .build());
    }

    // 댓글 수정 (본인만 가능)
    @Transactional
    public void update(UpdateCommentDto dto, Long userId) {
        PostComment comment = postCommentRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.COMMENT_UNAUTHORIZED);
        }

        comment.update(dto.getContent());
    }

    // 댓글 삭제 (본인만 가능)
    @Transactional
    public void delete(Long id, Long userId) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.COMMENT_UNAUTHORIZED);
        }

        postCommentRepository.delete(comment);
    }
}
