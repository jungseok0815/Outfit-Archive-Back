package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
