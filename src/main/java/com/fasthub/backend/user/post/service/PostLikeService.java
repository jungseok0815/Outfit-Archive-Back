package com.fasthub.backend.user.post.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostLike;
import com.fasthub.backend.user.post.repository.PostLikeRepository;
import com.fasthub.backend.user.post.repository.PostRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final AuthRepository authRepository;

    // 좋아요 토글 (좋아요 → 취소, 취소 → 좋아요)
    // 반환값: true = 좋아요 완료, false = 좋아요 취소
    @Transactional
    public boolean toggle(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_FAIL_SELECT));
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return postLikeRepository.findByPostIdAndUserId(postId, userId)
                .map(like -> {
                    postLikeRepository.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder()
                            .post(post)
                            .user(user)
                            .build());
                    return true;
                });
    }

    // 좋아요 수 조회
    public long count(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    // 특정 사용자의 좋아요 여부 확인
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
