package com.fasthub.backend.admin.order.repository;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.dto.RevenueByBrandDto;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.recommend.strategy.PopularProductProjection;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE (:keyword IS NULL OR :keyword = '' OR o.user.userNm LIKE %:keyword%) AND (:brandId IS NULL OR o.product.brand.id = :brandId)")
    Page<Order> findAllByKeyword(@Param("keyword") String keyword, @Param("brandId") Long brandId, Pageable pageable);

    // 특정 기간 내 상품별 주문 수 (인기도 계산용)
    @Query("SELECT o.product.id AS productId, COUNT(o) AS orderCount " +
           "FROM Order o " +
           "WHERE o.orderDate >= :since " +
           "GROUP BY o.product.id " +
           "ORDER BY orderCount DESC")
    List<PopularProductProjection> findPopularProductIds(@Param("since") LocalDateTime since, Pageable pageable);

    // 브랜드별 매출 집계 (brandId가 null이면 전체)
    @Query("SELECT new com.fasthub.backend.admin.order.dto.RevenueByBrandDto(o.product.brand.id, o.product.brand.brandNm, COUNT(o), SUM(o.totalPrice)) " +
           "FROM Order o " +
           "WHERE :brandId IS NULL OR o.product.brand.id = :brandId " +
           "GROUP BY o.product.brand.id, o.product.brand.brandNm")
    List<RevenueByBrandDto> findRevenueByBrand(@Param("brandId") Long brandId);

    // 유저의 최근 구매 이력 조회 (취소 제외, 상품+브랜드 fetch join)
    @Query("SELECT o FROM Order o JOIN FETCH o.product p LEFT JOIN FETCH p.brand " +
           "WHERE o.user.id = :userId AND o.orderDate >= :since AND o.status != 'CANCELLED'")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // 특정 카테고리/브랜드의 인기 상품 조회 (구매한 상품 제외)
    @Query("SELECT o.product.id AS productId, COUNT(o) AS orderCount " +
           "FROM Order o " +
           "WHERE o.orderDate >= :since " +
           "AND (o.product.category IN :categories OR o.product.brand.id IN :brandIds) " +
           "AND o.product.id NOT IN :excludeIds " +
           "GROUP BY o.product.id " +
           "ORDER BY orderCount DESC")
    List<PopularProductProjection> findPopularProductsByCategoriesOrBrands(
            @Param("categories") List<ProductCategory> categories,
            @Param("brandIds") List<Long> brandIds,
            @Param("excludeIds") List<Long> excludeIds,
            @Param("since") LocalDateTime since,
            Pageable pageable);

    // 특정 유저의 주문 수 (Cold Start 판별용)
    long countByUserId(Long userId);

    // 사용자 본인 주문 목록
    Page<Order> findByUser(User user, Pageable pageable);

    // 사용자 본인 주문 목록 (PENDING 제외)
    Page<Order> findByUserAndStatusNot(User user, OrderStatus status, Pageable pageable);

    // 토스 주문번호로 조회
    java.util.Optional<Order> findByTossOrderId(String tossOrderId);
}
