package com.fasthub.backend.admin.order.repository;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.user.recommend.strategy.PopularProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE :keyword IS NULL OR :keyword = '' OR o.user.userNm LIKE %:keyword%")
    Page<Order> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 특정 기간 내 상품별 주문 수 (인기도 계산용)
    @Query("SELECT o.product.id AS productId, COUNT(o) AS orderCount " +
           "FROM Order o " +
           "WHERE o.orderDate >= :since " +
           "GROUP BY o.product.id " +
           "ORDER BY orderCount DESC")
    List<PopularProductProjection> findPopularProductIds(@Param("since") LocalDateTime since, Pageable pageable);

    // 특정 유저의 주문 수 (Cold Start 판별용)
    long countByUserId(Long userId);
}
