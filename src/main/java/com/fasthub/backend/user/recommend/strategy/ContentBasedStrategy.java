package com.fasthub.backend.user.recommend.strategy;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.review.repository.ReviewStatsProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentBasedStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    // 구매 이력 조회 기간 (90일)
    private static final int HISTORY_DAYS = 90;
    // 인기도 기준 기간 (30일)
    private static final int POPULAR_DAYS = 30;
    // 상위 카테고리/브랜드 추출 개수
    private static final int TOP_N = 2;

    private static final double ORDER_WEIGHT  = 1.0;
    private static final double REVIEW_WEIGHT = 0.5;
    private static final double RATING_WEIGHT = 2.0;

    public List<RecommendProductDto> recommend(Long userId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(HISTORY_DAYS);

        // 1. 유저의 최근 구매 이력 조회
        List<Order> recentOrders = orderRepository.findRecentOrdersByUserId(userId, since);
        if (recentOrders.isEmpty()) {
            log.info("[ContentBased] userId={} 최근 구매 이력 없음", userId);
            return List.of();
        }

        // 2. 구매한 상품 ID 수집 (추천 결과에서 제외)
        List<Long> purchasedIds = recentOrders.stream()
                .map(o -> o.getProduct().getId())
                .distinct()
                .collect(toList());

        // 3. 카테고리 빈도 집계 → 상위 TOP_N 추출
        List<ProductCategory> topCategories = recentOrders.stream()
                .collect(groupingBy(o -> o.getProduct().getCategory(), counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<ProductCategory, Long>comparingByValue().reversed())
                .limit(TOP_N)
                .map(Map.Entry::getKey)
                .collect(toList());

        // 4. 브랜드 빈도 집계 → 상위 TOP_N 추출
        List<Long> topBrandIds = recentOrders.stream()
                .filter(o -> o.getProduct().getBrand() != null)
                .collect(groupingBy(o -> o.getProduct().getBrand().getId(), counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(TOP_N)
                .map(Map.Entry::getKey)
                .collect(toList());

        log.info("[ContentBased] userId={} topCategories={} topBrandIds={}", userId, topCategories, topBrandIds);

        // IN 절에 빈 리스트가 들어가면 SQL 오류 → 더미값으로 방어
        if (topBrandIds.isEmpty()) topBrandIds = List.of(-1L);

        // 5. 해당 카테고리/브랜드의 인기 상품 조회 (구매 이력 제외)
        LocalDateTime popularSince = LocalDateTime.now().minusDays(POPULAR_DAYS);
        List<PopularProductProjection> popular = orderRepository.findPopularProductsByCategoriesOrBrands(
                topCategories, topBrandIds, purchasedIds, popularSince,
                PageRequest.of(0, limit * 3));

        if (popular.isEmpty()) {
            log.info("[ContentBased] userId={} 조건에 맞는 인기 상품 없음", userId);
            return List.of();
        }

        // 6. 상품 상세 + 리뷰 통계 조회
        List<Long> productIds = popular.stream()
                .map(PopularProductProjection::getProductId)
                .collect(toList());

        Map<Long, Long> orderCountMap = popular.stream()
                .collect(toMap(PopularProductProjection::getProductId,
                               PopularProductProjection::getOrderCount));

        Map<Long, Product> productMap = productRepository.findAllByIdInWithImages(productIds).stream()
                .collect(toMap(Product::getId, p -> p));

        Map<Long, ReviewStatsProjection> reviewStatsMap = reviewRepository
                .findReviewStatsByProductIds(productIds).stream()
                .collect(toMap(ReviewStatsProjection::getProductId, s -> s));

        // 7. 점수 계산 후 정렬, 상위 limit개 반환
        return productIds.stream()
                .filter(productMap::containsKey)
                .map(id -> {
                    Product product = productMap.get(id);
                    long orderCnt = orderCountMap.getOrDefault(id, 0L);
                    ReviewStatsProjection stats = reviewStatsMap.get(id);
                    long reviewCnt  = stats != null ? stats.getReviewCount() : 0L;
                    double avgRating = stats != null && stats.getAvgRating() != null
                            ? Math.round(stats.getAvgRating() * 10.0) / 10.0 : 0.0;

                    String imgPath = (product.getImages() != null && !product.getImages().isEmpty())
                            ? product.getImages().get(0).getImgPath() : null;
                    return RecommendProductDto.builder()
                            .productId(product.getId())
                            .productNm(product.getProductNm())
                            .productCode(product.getProductCode())
                            .productPrice(product.getProductPrice())
                            .category(product.getCategory())
                            .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                            .imgPath(imgPath)
                            .orderCount(orderCnt)
                            .reviewCount(reviewCnt)
                            .avgRating(avgRating)
                            .reason("구매 이력 기반 추천")
                            .build();
                })
                .sorted(Comparator.comparingDouble(dto ->
                        -(dto.getOrderCount() * ORDER_WEIGHT
                        + dto.getReviewCount() * REVIEW_WEIGHT
                        + dto.getAvgRating() * RATING_WEIGHT)))
                .limit(limit)
                .collect(toList());
    }
}
