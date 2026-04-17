package com.fasthub.backend.user.productview;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.productview.entity.ProductView;
import com.fasthub.backend.user.productview.repository.ProductViewRepository;
import com.fasthub.backend.user.productview.service.ProductViewService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductViewService 테스트")
class ProductViewServiceTest {

    @InjectMocks
    private ProductViewService productViewService;

    @Mock
    private ProductViewRepository productViewRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ProductRepository productRepository;

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
                .category(ProductCategory.TOP)
                .build();
    }

    // ────────────────────────────────────────────────
    // 조회 기록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("조회 기록")
    class RecordView {

        @Test
        @DisplayName("성공 - 중복 아니면 저장")
        void recordView_success() {
            User user = buildUser();
            Product product = buildProduct();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(productViewRepository.existsByUserAndProductAndViewedAtAfter(
                    any(), any(), any(LocalDateTime.class))).willReturn(false);

            productViewService.recordView(1L, 10L);

            then(productViewRepository).should().save(any(ProductView.class));
        }

        @Test
        @DisplayName("스킵 - 1시간 내 중복 조회")
        void recordView_skip_duplicate() {
            User user = buildUser();
            Product product = buildProduct();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(productViewRepository.existsByUserAndProductAndViewedAtAfter(
                    any(), any(), any(LocalDateTime.class))).willReturn(true);

            productViewService.recordView(1L, 10L);

            then(productViewRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void recordView_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productViewService.recordView(999L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void recordView_fail_productNotFound() {
            User user = buildUser();

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productViewService.recordView(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_SELECT.getMessage());
        }
    }
}
