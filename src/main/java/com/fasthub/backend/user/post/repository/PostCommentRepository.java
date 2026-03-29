package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Page<PostComment> findByPostId(Long postId, Pageable pageable);
    void deleteByPost(Post post);
    long countByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostComment pc WHERE pc.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
