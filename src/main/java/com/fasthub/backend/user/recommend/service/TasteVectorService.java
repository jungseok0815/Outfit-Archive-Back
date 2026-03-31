package com.fasthub.backend.user.recommend.service;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.productview.repository.ProductViewRepository;
import com.fasthub.backend.user.wishlist.repository.WishlistRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TasteVectorService {

    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductViewRepository productViewRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIX        = "user:taste_vector:";
    private static final long   TTL_HOURS     = 1;
    private static final int    HISTORY_DAYS  = 90;
    private static final int    VIEW_DAYS     = 30;

    // 가중치
    private static final double ORDER_WEIGHT  = 5.0;
    private static final double WISH_WEIGHT   = 3.0;
    private static final double VIEW_WEIGHT   = 1.0;

    /**
     * Redis에서 취향 벡터 조회
     * 없으면 null 반환
     */
    public double[] getFromCache(Long userId) {
        String cached = redisTemplate.opsForValue().get(PREFIX + userId);
        if (cached == null) return null;
        try {
            List<Double> list = objectMapper.readValue(cached, new TypeReference<>() {});
            return list.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            log.warn("[TasteVector] 캐시 파싱 실패 userId={}", userId);
            return null;
        }
    }

    /**
     * 로그인 시 비동기로 취향 벡터 계산 후 Redis 저장
     * 오래 걸려도 로그인 응답에 영향 없음
     */
    @Async
    @Transactional(readOnly = true)
    public void computeAndCache(Long userId) {
        log.info("[TasteVector] ===== 취향 벡터 계산 시작 userId={} =====", userId);
        long startTime = System.currentTimeMillis();
        try {
            double[] vector = compute(userId);
            if (vector == null) {
                log.info("[TasteVector] userId={} 히스토리 없음 → 캐시 저장 안 함", userId);
                return;
            }
            String json = objectMapper.writeValueAsString(arrayToList(vector));
            redisTemplate.opsForValue().set(PREFIX + userId, json, TTL_HOURS, TimeUnit.HOURS);
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[TasteVector] ===== 취향 벡터 Redis 저장 완료 userId={} 소요시간={}ms =====", userId, elapsed);
            log.info("[TasteVector] Redis key={} 벡터 앞 5자리={}", PREFIX + userId,
                    vector.length >= 5
                            ? String.format("[%.4f, %.4f, %.4f, %.4f, %.4f, ...]", vector[0], vector[1], vector[2], vector[3], vector[4])
                            : "벡터 길이 부족");
        } catch (Exception e) {
            log.error("[TasteVector] userId={} 벡터 계산 실패: {}", userId, e.getMessage());
        }
    }

    /**
     * 캐시 무효화 (조회/찜 발생 시 호출)
     */
    public void evict(Long userId) {
        redisTemplate.delete(PREFIX + userId);
        log.debug("[TasteVector] userId={} 캐시 무효화", userId);
    }

    /**
     * 구매 + 찜 + 조회 기록으로 취향 벡터 계산 (가중 평균)
     *
     * 공식: (벡터A×가중치A + 벡터B×가중치B + ...) / (가중치A + 가중치B + ...)
     */
    @Transactional(readOnly = true)
    public double[] compute(Long userId) {
        LocalDateTime orderSince = LocalDateTime.now().minusDays(HISTORY_DAYS);
        LocalDateTime viewSince  = LocalDateTime.now().minusDays(VIEW_DAYS);

        List<double[]> vectors = new ArrayList<>();
        List<Double>   weights = new ArrayList<>();

        // 구매 이력 (가중치 5)
        var orders = orderRepository.findRecentOrdersByUserId(userId, orderSince);
        int orderWithEmbedding = 0;
        for (var order : orders) {
            double[] vec = parseEmbedding(order.getProduct());
            if (vec != null) {
                vectors.add(vec);
                weights.add(ORDER_WEIGHT);
                orderWithEmbedding++;
            }
        }
        log.info("[TasteVector] userId={} 구매 기록 전체={}건 / 임베딩 있음={}건 (가중치={})",
                userId, orders.size(), orderWithEmbedding, ORDER_WEIGHT);

        // 찜 목록 (가중치 3)
        var wishes = wishlistRepository.findByUserId(userId, PageRequest.of(0, 50));
        int wishWithEmbedding = 0;
        for (var wish : wishes) {
            double[] vec = parseEmbedding(wish.getProduct());
            if (vec != null) {
                vectors.add(vec);
                weights.add(WISH_WEIGHT);
                wishWithEmbedding++;
            }
        }
        log.info("[TasteVector] userId={} 찜 목록 전체={}건 / 임베딩 있음={}건 (가중치={})",
                userId, wishes.getNumberOfElements(), wishWithEmbedding, WISH_WEIGHT);

        // 조회 기록 (가중치 1)
        var views = productViewRepository.findRecentByUserId(userId, viewSince, PageRequest.of(0, 50));
        int viewWithEmbedding = 0;
        for (var view : views) {
            double[] vec = parseEmbedding(view.getProduct());
            if (vec != null) {
                vectors.add(vec);
                weights.add(VIEW_WEIGHT);
                viewWithEmbedding++;
            }
        }
        log.info("[TasteVector] userId={} 조회 기록 전체={}건 / 임베딩 있음={}건 (가중치={})",
                userId, views.size(), viewWithEmbedding, VIEW_WEIGHT);

        log.info("[TasteVector] userId={} 가중 평균 계산 대상 총 벡터={}개 가중치합={}",
                userId, vectors.size(),
                weights.stream().mapToDouble(Double::doubleValue).sum());

        if (vectors.isEmpty()) {
            log.info("[TasteVector] userId={} 임베딩 있는 히스토리 없음 → 계산 불가", userId);
            return null;
        }

        return weightedAverage(vectors, weights);
    }

    /**
     * 가중 평균 계산
     * 각 자리 = (vec[i]×weight + ...) / (weight 합계)
     */
    private double[] weightedAverage(List<double[]> vectors, List<Double> weights) {
        int dim = vectors.get(0).length;
        double[] result     = new double[dim];
        double   weightSum  = 0;

        for (int j = 0; j < vectors.size(); j++) {
            double   weight = weights.get(j);
            double[] vec    = vectors.get(j);
            weightSum += weight;
            for (int i = 0; i < dim; i++) {
                result[i] += vec[i] * weight;  // 분자: 벡터 × 가중치 누적
            }
        }

        // 분모: 가중치 합계로 나누기
        for (int i = 0; i < dim; i++) {
            result[i] /= weightSum;
        }

        log.info("[TasteVector] 가중 평균 완료 → 벡터 차원={} 가중치합={}", dim, weightSum);
        return result;
    }

    private double[] parseEmbedding(Product product) {
        if (product == null || product.getEmbedding() == null) return null;
        try {
            List<Double> list = objectMapper.readValue(product.getEmbedding(), new TypeReference<>() {});
            return list.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Double> arrayToList(double[] arr) {
        List<Double> list = new ArrayList<>(arr.length);
        for (double v : arr) list.add(v);
        return list;
    }
}
