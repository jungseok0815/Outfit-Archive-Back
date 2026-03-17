package com.fasthub.backend.user.wishlist.service;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import com.fasthub.backend.user.wishlist.dto.WishlistResponseDto;
import com.fasthub.backend.user.wishlist.entity.Wishlist;
import com.fasthub.backend.user.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    // 관심상품 토글 (추가/제거)
    // 반환값: true = 추가, false = 제거
    @Transactional
    public boolean toggle(Long productId, Long userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));

        return wishlistRepository.findByUserIdAndProductId(userId, productId)
                .map(wishlist -> {
                    wishlistRepository.delete(wishlist);
                    return false;
                })
                .orElseGet(() -> {
                    wishlistRepository.save(Wishlist.builder()
                            .user(user)
                            .product(product)
                            .build());
                    return true;
                });
    }

    // 관심상품 여부 확인
    public boolean isWished(Long productId, Long userId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    // 관심상품 목록 조회
    public Page<WishlistResponseDto> myWishlist(Long userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable)
                .map(WishlistResponseDto::of);
    }
}
