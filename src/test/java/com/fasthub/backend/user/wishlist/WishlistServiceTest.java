package com.fasthub.backend.user.wishlist;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import com.fasthub.backend.user.wishlist.dto.WishlistResponseDto;
import com.fasthub.backend.user.wishlist.entity.Wishlist;
import com.fasthub.backend.user.wishlist.repository.WishlistRepository;
import com.fasthub.backend.user.wishlist.service.WishlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistService 테스트")
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ProductRepository productRepository;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser() {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(10L)
                .productNm("테스트 상품")
                .productCode("P001")
                .productPrice(50000)
                .productQuantity(100)
                .category(null)
                .build();
    }

    // ────────────────────────────────────────────────
    // 관심상품 토글
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("관심상품 토글")
    class Toggle {

        @Test
        @DisplayName("성공 - 추가 (위시리스트에 없을 때 → true 반환)")
        void toggle_success_add() {
            User user = buildUser();
            Product product = buildProduct();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(wishlistRepository.findByUserIdAndProductId(1L, 10L)).willReturn(Optional.empty());
            given(wishlistRepository.save(any(Wishlist.class))).willReturn(mock(Wishlist.class));

            boolean result = wishlistService.toggle(10L, 1L);

            assertThat(result).isTrue();
            then(wishlistRepository).should().save(any(Wishlist.class));
        }

        @Test
        @DisplayName("성공 - 제거 (위시리스트에 있을 때 → false 반환)")
        void toggle_success_remove() {
            User user = buildUser();
            Product product = buildProduct();
            Wishlist wishlist = mock(Wishlist.class);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(wishlistRepository.findByUserIdAndProductId(1L, 10L)).willReturn(Optional.of(wishlist));

            boolean result = wishlistService.toggle(10L, 1L);

            assertThat(result).isFalse();
            then(wishlistRepository).should().delete(wishlist);
            then(wishlistRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void toggle_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> wishlistService.toggle(10L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void toggle_fail_productNotFound() {
            User user = buildUser();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> wishlistService.toggle(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_SELECT.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 관심상품 여부 확인
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("관심상품 여부 확인")
    class IsWished {

        @Test
        @DisplayName("관심상품 등록됨 → true")
        void isWished_true() {
            given(wishlistRepository.existsByUserIdAndProductId(1L, 10L)).willReturn(true);

            boolean result = wishlistService.isWished(10L, 1L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("관심상품 미등록 → false")
        void isWished_false() {
            given(wishlistRepository.existsByUserIdAndProductId(1L, 10L)).willReturn(false);

            boolean result = wishlistService.isWished(10L, 1L);

            assertThat(result).isFalse();
        }
    }

    // ────────────────────────────────────────────────
    // 관심상품 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("관심상품 목록 조회")
    class MyWishlist {

        @Test
        @DisplayName("성공 - 목록 반환")
        void myWishlist_success() {
            Pageable pageable = PageRequest.of(0, 10);

            // Wishlist와 Product를 mock으로 처리 (WishlistResponseDto.of 내부에서 getImages() 호출)
            Product mockProduct = mock(Product.class);
            given(mockProduct.getId()).willReturn(10L);
            given(mockProduct.getProductNm()).willReturn("테스트 상품");
            given(mockProduct.getProductPrice()).willReturn(50000);
            given(mockProduct.getBrand()).willReturn(null);
            given(mockProduct.getImages()).willReturn(List.of());

            Wishlist mockWishlist = mock(Wishlist.class);
            given(mockWishlist.getId()).willReturn(1L);
            given(mockWishlist.getProduct()).willReturn(mockProduct);

            given(wishlistRepository.findByUserId(1L, pageable))
                    .willReturn(new PageImpl<>(List.of(mockWishlist)));

            Page<WishlistResponseDto> result = wishlistService.myWishlist(1L, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getProductNm()).isEqualTo("테스트 상품");
            assertThat(result.getContent().get(0).getProductPrice()).isEqualTo(50000);
        }

        @Test
        @DisplayName("성공 - 목록 없음")
        void myWishlist_success_empty() {
            Pageable pageable = PageRequest.of(0, 10);

            given(wishlistRepository.findByUserId(1L, pageable))
                    .willReturn(new PageImpl<>(List.of()));

            Page<WishlistResponseDto> result = wishlistService.myWishlist(1L, pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }
}
