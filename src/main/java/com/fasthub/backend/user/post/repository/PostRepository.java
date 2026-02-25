package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE :keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword%")
    Page<Post> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
