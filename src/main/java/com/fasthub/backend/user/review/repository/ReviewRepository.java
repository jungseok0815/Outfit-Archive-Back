package com.fasthub.backend.user.review.repository;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrder(Order order);

    // 특정 유저의 리뷰가 있는 orderId 목록 (N+1 방지용 일괄 조회)
    @Query("SELECT r.order.id FROM Review r WHERE r.order.id IN :orderIds")
    List<Long> findReviewedOrderIds(@Param("orderIds") List<Long> orderIds);

    Page<Review> findByProduct(Product product, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.product = :product")
    void deleteByProduct(@Param("product") Product product);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 상품 ID 목록에 대한 리뷰 수 / 평균 평점 일괄 조회
    @Query("SELECT r.product.id AS productId, COUNT(r) AS reviewCount, AVG(r.rating) AS avgRating " +
           "FROM Review r " +
           "WHERE r.product.id IN :productIds " +
           "GROUP BY r.product.id")
    List<ReviewStatsProjection> findReviewStatsByProductIds(@Param("productIds") List<Long> productIds);
}
