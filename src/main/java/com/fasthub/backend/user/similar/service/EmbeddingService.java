package com.fasthub.backend.user.similar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.similar.client.EmbeddingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service  // AI 임베딩 기능 비활성화
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingClient embeddingClient;
    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ObjectMapper objectMapper;

    @Async("embeddingExecutor")
    @Transactional
    public void generateAndSave(Long productId) {
        try {
            Product product = productRepository.findById(productId).orElseThrow();

            List<ProductImg> images = productImgRepository.findByProduct(product);
            if (images.isEmpty()) {
                log.warn("[Embedding] 이미지 없음, 건너뜀 productId={}", productId);
                return;
            }

            // 첫 번째 이미지로 벡터 생성
            String imagePath = images.get(0).getImgPath();
            float[] vector = embeddingClient.embed(imagePath);

            String json = objectMapper.writeValueAsString(vector);
            product.updateEmbedding(json);
            productRepository.save(product);

            log.info("[Embedding] 완료 productId={}", productId);
        } catch (Exception e) {
            log.error("[Embedding] 실패 productId={}, error={}", productId, e.getMessage());
        }
    }
}
