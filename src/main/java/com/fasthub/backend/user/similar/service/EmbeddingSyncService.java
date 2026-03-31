package com.fasthub.backend.user.similar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.user.recommend.client.ClipClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingSyncService {

    private final ClipClient clipClient;
    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void generateAndSaveSync(Long productId) throws Exception {
        Product product = productRepository.findById(productId).orElseThrow();

        List<ProductImg> images = productImgRepository.findByProduct(product);
        if (images.isEmpty()) {
            log.warn("[Embedding] 이미지 없음 → 건너뜀 productId={}", productId);
            return;
        }

        String imageUrl = images.get(0).getImgPath();
        List<Double> vector = clipClient.extractVector(productId, imageUrl);

        if (vector.isEmpty()) {
            log.warn("[Embedding] 빈 벡터 반환 productId={}", productId);
            return;
        }

        String json = objectMapper.writeValueAsString(vector);
        product.updateEmbedding(json);
        productRepository.save(product);
    }
}
