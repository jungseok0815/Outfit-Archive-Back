package com.fasthub.backend.user.recommend.service;

import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.strategy.ContentBasedStrategy;
import com.fasthub.backend.user.recommend.strategy.PopularityStrategy;
import com.fasthub.backend.user.recommend.strategy.VectorBasedStrategy;
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

    private final PopularityStrategy popularityStrategy;
    private final ContentBasedStrategy contentBasedStrategy;
    private final VectorBasedStrategy vectorBasedStrategy;

    public List<RecommendProductDto> recommendPopular(int limit) {
        log.info("[Recommend] 인기 상품 추천 (로그인 무관)");
        return popularityStrategy.recommend(limit);
    }

    // 일반 추천: 비로그인 → 인기 상품 / 로그인 → 조회 기반
    public List<RecommendProductDto> recommend(Long userId, int limit) {
        if (userId == null) {
            log.info("[Recommend] 비로그인 사용자 → 인기 상품 추천");
            return popularityStrategy.recommend(limit);
        }

        log.info("[Recommend] 로그인 사용자 userId={} → 조회 기반 추천 시도", userId);
        List<RecommendProductDto> viewBased = contentBasedStrategy.recommendFromViews(userId, limit);
        if (!viewBased.isEmpty()) {
            log.info("[Recommend] 조회 기반 추천 성공 {}건", viewBased.size());
            return viewBased;
        }

        log.info("[Recommend] 조회 기록 없음 → 인기 상품 추천");
        return popularityStrategy.recommend(limit);
    }

    // AI 추천: 벡터 기반 / 벡터 계산 불가 시 인기 상품
    public List<RecommendProductDto> recommendAi(Long userId, int limit, int page) {
        log.info("[Recommend] AI 추천 요청 userId={} page={}", userId, page);
        List<RecommendProductDto> vectorBased = vectorBasedStrategy.recommend(userId, limit, page);
        if (!vectorBased.isEmpty()) {
            log.info("[Recommend] 벡터 기반 추천 성공 {}건", vectorBased.size());
            return vectorBased;
        }

        log.info("[Recommend] 벡터 계산 불가 → 인기 상품 추천");
        return popularityStrategy.recommend(limit, page);
    }
}
