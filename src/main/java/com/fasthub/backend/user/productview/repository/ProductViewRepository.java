package com.fasthub.backend.user.productview.repository;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.productview.entity.ProductView;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {

    // 중복 조회 방지: 특정 시간 이후 같은 유저+상품 조회 기록 존재 여부
    boolean existsByUserAndProductAndViewedAtAfter(User user, Product product, LocalDateTime since);

    // Cold Start 판단: 최근 N일 내 조회 기록 수
    long countByUserIdAndViewedAtAfter(Long userId, LocalDateTime since);

    // AI 추천: 유저의 최근 조회 기록 (시간 기준)
    @Query("SELECT pv FROM ProductView pv JOIN FETCH pv.product p LEFT JOIN FETCH p.brand WHERE pv.user.id = :userId AND pv.viewedAt >= :since ORDER BY pv.viewedAt DESC")
    List<ProductView> findRecentByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since, Pageable pageable);

    // 유저 삭제 시 일괄 삭제
    @Modifying
    @Query("DELETE FROM ProductView pv WHERE pv.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // 상품 삭제 시 일괄 삭제
    @Modifying
    @Query("DELETE FROM ProductView pv WHERE pv.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    // 관리자 통계: 기간별 상품 조회수 (상위 N개)
    @Query("SELECT pv.product.id AS productId, COUNT(pv) AS viewCount FROM ProductView pv WHERE pv.viewedAt >= :since GROUP BY pv.product.id ORDER BY COUNT(pv) DESC")
    List<ProductViewStatsProjection> findTopViewedProductIds(@Param("since") LocalDateTime since, Pageable pageable);
}
