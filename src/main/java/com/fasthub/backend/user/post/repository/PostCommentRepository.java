package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Page<PostComment> findByPostId(Long postId, Pageable pageable);
    void deleteByPost(Post post);
    long countByPostId(Long postId);
}
