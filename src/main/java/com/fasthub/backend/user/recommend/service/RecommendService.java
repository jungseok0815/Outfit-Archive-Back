package com.fasthub.backend.user.recommend.service;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.user.productview.repository.ProductViewRepository;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.strategy.ContentBasedStrategy;
import com.fasthub.backend.user.recommend.strategy.PopularityStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecommendService {

    private final OrderRepository orderRepository;
    private final ProductViewRepository productViewRepository;
    private final PopularityStrategy popularityStrategy;
    private final ContentBasedStrategy contentBasedStrategy;

    public List<RecommendProductDto> recommendPopular(int limit) {
        log.info("[Recommend] 인기 상품 추천 (로그인 무관)");
        return popularityStrategy.recommend(limit);
    }

    public List<RecommendProductDto> recommend(Long userId, int limit) {
        // 비로그인 → 인기 상품
        if (userId == null) {
            log.info("[Recommend] 비로그인 사용자 → 인기 상품 추천");
            return popularityStrategy.recommend(limit);
        }

        long orderCount = orderRepository.countByUserId(userId);
        log.info("[Recommend] userId={}, orderCount={}", userId, orderCount);

        // Cold Start (구매 이력 없음) → 조회 기록 확인 후 조회 기반 추천, 없으면 인기 상품
        if (orderCount == 0) {
            long viewCount = productViewRepository.countByUserIdAndViewedAtAfter(userId, LocalDateTime.now().minusDays(30));
            if (viewCount > 0) {
                log.info("[Recommend] Cold Start (조회 기록 {}건) → 조회 기반 추천", viewCount);
                List<RecommendProductDto> viewBased = contentBasedStrategy.recommendFromViews(userId, limit);
                if (!viewBased.isEmpty()) {
                    if (viewBased.size() >= limit) return viewBased;
                    Set<Long> existingIds = viewBased.stream().map(RecommendProductDto::getProductId).collect(Collectors.toSet());
                    List<RecommendProductDto> result = new ArrayList<>(viewBased);
                    popularityStrategy.recommend(limit).stream()
                            .filter(p -> !existingIds.contains(p.getProductId()))
                            .limit(limit - viewBased.size())
                            .forEach(result::add);
                    return result;
                }
            }
            log.info("[Recommend] Cold Start → 인기 상품 추천");
            return popularityStrategy.recommend(limit);
        }

        // 구매 이력 있음 → 콘텐츠 기반 추천
        log.info("[Recommend] 구매 이력 있음 → 콘텐츠 기반 추천");
        List<RecommendProductDto> contentBased = contentBasedStrategy.recommend(userId, limit);

        if (contentBased.size() >= limit) {
            return contentBased;
        }

        // 콘텐츠 기반 결과가 부족하면 인기 상품으로 보완
        log.info("[Recommend] 콘텐츠 기반 추천 부족 ({}/{}), 인기 상품으로 보완", contentBased.size(), limit);
        Set<Long> existingIds = contentBased.stream()
                .map(RecommendProductDto::getProductId)
                .collect(Collectors.toSet());

        List<RecommendProductDto> result = new ArrayList<>(contentBased);
        popularityStrategy.recommend(limit).stream()
                .filter(p -> !existingIds.contains(p.getProductId()))
                .limit(limit - contentBased.size())
                .forEach(result::add);

        return result;
    }
}
