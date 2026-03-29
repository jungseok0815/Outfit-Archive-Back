package com.fasthub.backend.user.post.repository;

import com.fasthub.backend.user.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE :keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword%")
    Page<Post> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
           "LEFT JOIN p.postProducts pp " +
           "LEFT JOIN pp.product prod " +
           "LEFT JOIN prod.brand b " +
           "WHERE b.brandNm LIKE %:keyword% OR p.title LIKE %:keyword%")
    Page<Post> findAllByBrandKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Post> findByUser_Id(Long userId, Pageable pageable);

    long countByUser_Id(Long userId);

    List<Post> findAllByUser_Id(Long userId);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.postProducts pp WHERE pp.product.id = :productId")
    Page<Post> findByProductId(@Param("productId") Long productId, Pageable pageable);
}
