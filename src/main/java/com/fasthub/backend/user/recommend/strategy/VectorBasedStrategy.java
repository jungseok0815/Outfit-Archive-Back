package com.fasthub.backend.user.recommend.strategy;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.service.TasteVectorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorBasedStrategy {

    private final ProductRepository productRepository;
    private final TasteVectorService tasteVectorService;
    private final ObjectMapper objectMapper;

    public List<RecommendProductDto> recommend(Long userId, int limit, int page, Set<Long> excludeIds) {
        // 1. Redis 캐시에서 취향 벡터 조회 → 없으면 실시간 계산
        double[] tasteVector = tasteVectorService.getFromCache(userId);
        if (tasteVector == null) {
            log.info("[VectorBased] userId={} Redis 캐시 없음 → 실시간 계산", userId);
            tasteVector = tasteVectorService.compute(userId);
        }

        if (tasteVector == null) {
            log.info("[VectorBased] userId={} 취향 벡터 계산 불가 (히스토리 없음)", userId);
            return List.of();
        }

        // 2. 전체 상품 임베딩과 유사도 계산 → 관련도 순 정렬 → 페이지 적용
        final double[] finalTasteVector = tasteVector;
        List<Product> allProducts = productRepository.findAllWithEmbedding();

        List<RecommendProductDto> result = allProducts.stream()
                .filter(p -> !excludeIds.contains(p.getId()))
                .map(p -> {
                    double[] vec = parseVector(p.getEmbedding());
                    double similarity = cosineSimilarity(finalTasteVector, vec);
                    String imgPath = (p.getImages() != null && !p.getImages().isEmpty())
                            ? p.getImages().get(0).getImgPath() : null;
                    return RecommendProductDto.builder()
                            .productId(p.getId())
                            .productNm(p.getProductNm())
                            .productCode(p.getProductCode())
                            .productPrice(p.getProductPrice())
                            .category(p.getCategory())
                            .brandNm(p.getBrand() != null ? p.getBrand().getBrandNm() : null)
                            .imgPath(imgPath)
                            .orderCount(0L)
                            .reviewCount(0L)
                            .avgRating(similarity)
                            .reason("AI 이미지 기반 추천")
                            .build();
                })
                .sorted(Comparator.comparingDouble(dto -> -dto.getAvgRating()))
                .skip((long) page * limit)
                .limit(limit)
                .collect(Collectors.toList());

        log.info("[VectorBased] userId={} page={} 추천 결과 {}건", userId, page, result.size());
        return result;
    }

    private double[] parseVector(String json) {
        try {
            List<Double> list = objectMapper.readValue(json, new TypeReference<>() {});
            return list.stream().mapToDouble(Double::doubleValue).toArray();
        } catch (Exception e) {
            return new double[0];
        }
    }

    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length == 0 || a.length != b.length) return 0.0;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
