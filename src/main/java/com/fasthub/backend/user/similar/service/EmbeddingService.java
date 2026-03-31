package com.fasthub.backend.user.similar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.recommend.client.ClipClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final ClipClient clipClient;
    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ObjectMapper objectMapper;
    private final EmbeddingSyncService embeddingSyncService;

    @Async("embeddingExecutor")
    @Transactional
    public void generateAndSave(Long productId) {
        log.info("[Embedding] ===== 벡터 추출 시작 productId={} =====", productId);
        long startTime = System.currentTimeMillis();
        try {
            Product product = productRepository.findById(productId).orElseThrow();

            List<ProductImg> images = productImgRepository.findByProduct(product);
            if (images.isEmpty()) {
                log.warn("[Embedding] 이미지 없음 → 벡터 추출 건너뜀 productId={}", productId);
                return;
            }

            // 첫 번째 이미지 S3 URL로 FastAPI에 벡터 추출 요청
            String imageUrl = images.get(0).getImgPath();
            log.info("[Embedding] FastAPI 벡터 추출 요청 productId={} imageUrl={}", productId, imageUrl);

            List<Double> vector = clipClient.extractVector(productId, imageUrl);

            if (vector.isEmpty()) {
                log.warn("[Embedding] FastAPI에서 빈 벡터 반환 productId={}", productId);
                return;
            }

            String json = objectMapper.writeValueAsString(vector);
            product.updateEmbedding(json);
            productRepository.save(product);

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[Embedding] ===== 벡터 저장 완료 productId={} 차원={} 소요시간={}ms =====",
                    productId, vector.size(), elapsed);
            log.info("[Embedding] 벡터 앞 5자리=[{}, {}, {}, {}, {}, ...]",
                    round(vector.get(0)), round(vector.get(1)), round(vector.get(2)),
                    round(vector.get(3)), round(vector.get(4)));

        } catch (Exception e) {
            log.error("[Embedding] 벡터 추출 실패 productId={} error={}", productId, e.getMessage());
        }
    }

    // 벌크 등록 시 순차 처리 (FastAPI 과부하 방지)
    @Async("embeddingExecutor")
    public void generateAndSaveBatch(List<Long> productIds) {
        log.info("[Embedding] ===== 배치 벡터 추출 시작 총{}건 =====", productIds.size());
        int success = 0;
        int fail    = 0;

        for (Long productId : productIds) {
            try {
                embeddingSyncService.generateAndSaveSync(productId);
                success++;
                log.info("[Embedding] 배치 진행 {}/{} productId={} ✅",
                        success + fail, productIds.size(), productId);
            } catch (Exception e) {
                fail++;
                log.error("[Embedding] 배치 실패 {}/{} productId={} error={}",
                        success + fail, productIds.size(), productId, e.getMessage());
            }
        }

        log.info("[Embedding] ===== 배치 완료 성공={} 실패={} =====", success, fail);
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
