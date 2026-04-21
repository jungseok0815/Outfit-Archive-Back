package com.fasthub.backend.user.recommend.strategy;

import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.review.repository.ReviewStatsProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopularityStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    private static final int RECENT_DAYS = 30;
    private static final int FALLBACK_DAYS = 90;

    // 점수 가중치: 주문 수(1.0) + 리뷰 수(0.5) + 평균 평점(2.0)
    // 예) 주문 10건 + 리뷰 4개 + 평점 4.5 → 10 + 2 + 9 = 21점
    private static final double ORDER_WEIGHT  = 1.0;
    private static final double REVIEW_WEIGHT = 0.5;
    private static final double RATING_WEIGHT = 2.0;

    public List<RecommendProductDto> recommend(int limit) {
        return recommend(limit, 0);
    }

    public List<RecommendProductDto> recommend(int limit, int page) {
        return recommend(limit, page, Set.of());
    }

    public List<RecommendProductDto> recommend(int limit, int page, Set<Long> excludeIds) {
        log.info("[Recommend] limit={} page={} excludeIds={}", limit, page, excludeIds.size());
        // 1순위: 최근 30일 인기 상품
        List<RecommendProductDto> result = fetchPopular(RECENT_DAYS, limit, page, excludeIds, "최근 30일 인기 상품");

        // 2순위: 30일 결과가 부족하면 90일로 확장
        if (result.size() < limit) {
            log.info("[Recommend] 30일 인기 상품 부족 ({}/{}), 90일로 확장", result.size(), limit);
            result = fetchPopular(FALLBACK_DAYS, limit, page, excludeIds, "최근 3개월 인기 상품");
        }

        // 3순위: 주문 이력 자체가 없으면 최신 상품 순으로 반환
        if (result.isEmpty()) {
            log.info("[Recommend] 주문 이력 없음, 최신 상품으로 대체");
            result = fetchLatest(limit, page, excludeIds);
        }

        return result;
    }

    private List<RecommendProductDto> fetchPopular(int days, int limit, int page, Set<Long> excludeIds, String reason) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        log.info("since : {}" , since);

        // 후보 풀: 현재 페이지까지 커버할 수 있도록 충분한 수 조회
        int poolSize = (page + 3) * limit;
        List<PopularProductProjection> projections =
                orderRepository.findPopularProductIds(since, PageRequest.of(0, poolSize));


        projections.forEach(item -> log.info("projection : {}", projections));

        if (projections.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = projections.stream()
                .map(PopularProductProjection::getProductId)
                .collect(Collectors.toList());

        // productId → orderCount 맵
        Map<Long, Long> orderCountMap = projections.stream()
                .collect(Collectors.toMap(
                        PopularProductProjection::getProductId,
                        PopularProductProjection::getOrderCount
                ));

        // 상품 상세 정보 일괄 조회 (이미지 포함)
        Map<Long, Product> productMap = productRepository.findAllByIdInWithImages(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // productId → 리뷰 수 / 평균 평점 맵
        Map<Long, ReviewStatsProjection> reviewStatsMap = reviewRepository
                .findReviewStatsByProductIds(productIds).stream()
                .collect(Collectors.toMap(ReviewStatsProjection::getProductId, s -> s));

        // 종합 점수 계산 후 내림차순 정렬, 상위 limit개 반환
        // score = (주문 수 × 1.0) + (리뷰 수 × 0.5) + (평균 평점 × 2.0)
        return productIds.stream()
                .filter(productMap::containsKey)
                .filter(id -> !excludeIds.contains(id))
                .map(id -> {
                    Product product = productMap.get(id);
                    long orderCnt = orderCountMap.getOrDefault(id, 0L);
                    ReviewStatsProjection stats = reviewStatsMap.get(id);
                    ResponseCategoryDto responseCategoryDto = null;
                    long reviewCnt = stats != null ? stats.getReviewCount() : 0L;
                    double avgRating = stats != null && stats.getAvgRating() != null
                            ? Math.round(stats.getAvgRating() * 10.0) / 10.0 : 0.0;
                    double score = (orderCnt * ORDER_WEIGHT)
                            + (reviewCnt * REVIEW_WEIGHT)
                            + (avgRating * RATING_WEIGHT);
                    log.debug("[Recommend] productId={} score={} (order={}, review={}, rating={})",
                            id, score, orderCnt, reviewCnt, avgRating);
                    String imgPath = (product.getImages() != null && !product.getImages().isEmpty())
                            ? product.getImages().get(0).getImgPath() : null;
                    return RecommendProductDto.builder()
                            .productId(product.getId())
                            .productNm(product.getProductNm())
                            .productCode(product.getProductCode())
                            .productPrice(product.getProductPrice())
                            .category(responseCategoryDto)
                            .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                            .imgPath(imgPath)
                            .orderCount(orderCnt)
                            .reviewCount(reviewCnt)
                            .avgRating(avgRating)
                            .reason(reason)
                            .build();
                })
                .sorted(Comparator.comparingDouble(dto ->
                        -(dto.getOrderCount() * ORDER_WEIGHT
                        + dto.getReviewCount() * REVIEW_WEIGHT
                        + dto.getAvgRating() * RATING_WEIGHT)))
                .skip((long) page * limit)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<RecommendProductDto> fetchLatest(int limit, int page, Set<Long> excludeIds) {
        return productRepository.findAll(PageRequest.of(page, limit,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC, "id")))
                .stream()
                .filter(p -> !excludeIds.contains(p.getId()))
                .map(p -> RecommendProductDto.of(p, 0L))
                .collect(Collectors.toList());
    }
}
