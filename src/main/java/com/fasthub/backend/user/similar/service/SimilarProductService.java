package com.fasthub.backend.user.similar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.similar.dto.SimilarProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// @Service  // AI 유사 상품 기능 비활성화
@RequiredArgsConstructor
@Slf4j
// @Transactional(readOnly = true)
public class SimilarProductService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public List<SimilarProductDto> findSimilar(Long productId, int limit) {
        Product target = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));

        if (target.getEmbedding() == null) {
            log.warn("[Similar] 임베딩 없음 productId={}", productId);
            return List.of();
        }

        float[] targetVec = parseVector(target.getEmbedding());

        return productRepository.findAll().stream()
                .filter(p -> !p.getId().equals(productId))
                .filter(p -> p.getEmbedding() != null)
                .map(p -> SimilarProductDto.of(p, cosineSimilarity(targetVec, parseVector(p.getEmbedding()))))
                .sorted(Comparator.comparingDouble(SimilarProductDto::getSimilarity).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private float[] parseVector(String json) {
        try {
            return objectMapper.readValue(json, float[].class);
        } catch (Exception e) {
            throw new RuntimeException("벡터 파싱 실패", e);
        }
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot   += (double) a[i] * b[i];
            normA += (double) a[i] * a[i];
            normB += (double) b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
