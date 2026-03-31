package com.fasthub.backend.admin.product.event;

import com.fasthub.backend.user.similar.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSavedEventListener {

    private final EmbeddingService embeddingService;

    // 단건 등록: 커밋 완료 후 비동기 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductSaved(ProductSavedEvent event) {
        log.info("[ProductEvent] 단건 커밋 완료 → 벡터 추출 productId={}", event.productId());
        embeddingService.generateAndSave(event.productId());
    }

    // 벌크 등록: 커밋 완료 후 순차 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBulkProductSaved(BulkProductSavedEvent event) {
        log.info("[ProductEvent] 벌크 커밋 완료 → 순차 벡터 추출 시작 총{}건", event.productIds().size());
        embeddingService.generateAndSaveBatch(event.productIds());
    }
}
