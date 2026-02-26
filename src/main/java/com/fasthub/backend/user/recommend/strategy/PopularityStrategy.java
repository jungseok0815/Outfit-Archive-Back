package com.fasthub.backend.user.recommend.strategy;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopularityStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private static final int RECENT_DAYS = 30;
    private static final int FALLBACK_DAYS = 90;

    public List<RecommendProductDto> recommend(int limit) {
        // 1순위: 최근 30일 인기 상품
        List<RecommendProductDto> result = fetchPopular(RECENT_DAYS, limit, "최근 30일 인기 상품");

        // 2순위: 30일 결과가 부족하면 90일로 확장
        if (result.size() < limit) {
            log.info("[Recommend] 30일 인기 상품 부족 ({}/{}), 90일로 확장", result.size(), limit);
            result = fetchPopular(FALLBACK_DAYS, limit, "최근 3개월 인기 상품");
        }

        // 3순위: 주문 이력 자체가 없으면 최신 상품 순으로 반환
        if (result.isEmpty()) {
            log.info("[Recommend] 주문 이력 없음, 최신 상품으로 대체");
            result = fetchLatest(limit);
        }

        return result;
    }

    private List<RecommendProductDto> fetchPopular(int days, int limit, String reason) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        // 인기 순위 조회 (productId, orderCount)
        List<PopularProductProjection> projections =
                orderRepository.findPopularProductIds(since, PageRequest.of(0, limit));

        if (projections.isEmpty()) {
            return List.of();
        }

        // productId → orderCount 맵 생성
        Map<Long, Long> orderCountMap = projections.stream()
                .collect(Collectors.toMap(
                        PopularProductProjection::getProductId,
                        PopularProductProjection::getOrderCount
                ));

        // 상품 상세 정보 일괄 조회
        List<Long> productIds = projections.stream()
                .map(PopularProductProjection::getProductId)
                .collect(Collectors.toList());

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 인기 순서 유지하면서 DTO 변환
        return productIds.stream()
                .filter(productMap::containsKey)
                .map(id -> {
                    Product product = productMap.get(id);
                    long count = orderCountMap.getOrDefault(id, 0L);
                    return RecommendProductDto.builder()
                            .productId(product.getId())
                            .productNm(product.getProductNm())
                            .productCode(product.getProductCode())
                            .productPrice(product.getProductPrice())
                            .category(product.getCategory())
                            .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                            .orderCount(count)
                            .reason(reason)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<RecommendProductDto> fetchLatest(int limit) {
        return productRepository.findAll(PageRequest.of(0, limit,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC, "id")))
                .stream()
                .map(p -> RecommendProductDto.of(p, 0L))
                .collect(Collectors.toList());
    }
}
