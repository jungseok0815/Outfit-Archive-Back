package com.fasthub.backend.user.productview.service;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.productview.entity.ProductView;
import com.fasthub.backend.user.productview.repository.ProductViewRepository;
import com.fasthub.backend.user.productview.repository.ProductViewStatsProjection;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductViewService {

    // 동일 유저+상품 중복 기록 방지 간격 (1시간)
    private static final int DEDUP_HOURS = 1;

    private final ProductViewRepository productViewRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void recordView(Long userId, Long productId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));

        // 1시간 이내 동일 조합이 이미 있으면 스킵
        LocalDateTime dedupSince = LocalDateTime.now().minusHours(DEDUP_HOURS);
        if (productViewRepository.existsByUserAndProductAndViewedAtAfter(user, product, dedupSince)) {
            log.debug("[ProductView] 중복 조회 스킵 userId={} productId={}", userId, productId);
            return;
        }

        productViewRepository.save(ProductView.builder()
                .user(user)
                .product(product)
                .viewedAt(LocalDateTime.now())
                .build());
        log.debug("[ProductView] 기록 완료 userId={} productId={}", userId, productId);
    }

    @Transactional(readOnly = true)
    public List<ProductView> getRecentViews(Long userId, int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return productViewRepository.findRecentByUserId(userId, since, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<ProductViewStatsProjection> getTopViewedProducts(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return productViewRepository.findTopViewedProductIds(since, PageRequest.of(0, limit));
    }
}
