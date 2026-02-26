package com.fasthub.backend.user.recommend.service;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.strategy.PopularityStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecommendService {

    private final OrderRepository orderRepository;
    private final PopularityStrategy popularityStrategy;

    public List<RecommendProductDto> recommend(Long userId, int limit) {
        long orderCount = orderRepository.countByUserId(userId);
        log.info("[Recommend] userId={}, orderCount={}", userId, orderCount);

        // Cold Start: 구매 이력이 없으면 인기 상품 추천
        if (orderCount == 0) {
            log.info("[Recommend] Cold Start 적용 → 인기 상품 추천");
            return popularityStrategy.recommend(limit);
        }

        // 추후 Content-Based, Collaborative Filtering 추가 예정
        log.info("[Recommend] 구매 이력 있음 → 추후 개인화 추천 적용 예정, 임시로 인기 상품 반환");
        return popularityStrategy.recommend(limit);
    }

}
