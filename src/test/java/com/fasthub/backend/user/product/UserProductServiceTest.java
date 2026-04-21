package com.fasthub.backend.user.product;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.product.repository.UserProductRepository;
import com.fasthub.backend.user.product.service.UserProductService;
import com.fasthub.backend.user.recommend.strategy.PopularProductProjection;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.review.repository.ReviewStatsProjection;
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
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProductService 테스트")
class UserProductServiceTest {

    @InjectMocks
    private UserProductService userProductService;

    @Mock
    private UserProductRepository userProductRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    private Product buildProduct(Long id) {
        return Product.builder()
                .id(id)
                .productNm("테스트 상품")
                .productCode("P00" + id)
                .productPrice(50000)
                .productQuantity(100)
                .category(null)
                .build();
    }

    // ────────────────────────────────────────────────
    // 상품 검색
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("상품 검색")
    class Search {

        @Test
        @DisplayName("성공 - 인기순 정렬")
        void search_success_popular() {
            Pageable pageable = PageRequest.of(0, 12);
            Product product = buildProduct(1L);
            ResponseProductDto dto = new ResponseProductDto();

            given(userProductRepository.searchProductsByPopularity(any(), any(), any(), any(), any(), any()))
                    .willReturn(new PageImpl<>(List.of(product)));
            given(reviewRepository.findReviewStatsByProductIds(any())).willReturn(List.of());
            given(orderRepository.findOrderCountsByProductIds(any())).willReturn(List.of());
            given(productMapper.productToProductDto(product)).willReturn(dto);

            Page<ResponseProductDto> result = userProductService.search(
                    "", null, null, null, null, "popular", pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 일반 정렬")
        void search_success_normal() {
            Pageable pageable = PageRequest.of(0, 12);
            Product product = buildProduct(1L);
            ResponseProductDto dto = new ResponseProductDto();

            given(userProductRepository.searchProducts(any(), any(), any(), any(), any(), any()))
                    .willReturn(new PageImpl<>(List.of(product)));
            given(reviewRepository.findReviewStatsByProductIds(any())).willReturn(List.of());
            given(orderRepository.findOrderCountsByProductIds(any())).willReturn(List.of());
            given(productMapper.productToProductDto(product)).willReturn(dto);

            Page<ResponseProductDto> result = userProductService.search(
                    "", null, null, null, null, "latest", pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 리뷰/주문 수 매핑")
        void search_success_withReviewAndOrderCount() {
            Pageable pageable = PageRequest.of(0, 12);
            Product product = buildProduct(1L);
            ResponseProductDto dto = new ResponseProductDto();

            ReviewStatsProjection reviewStats = mock(ReviewStatsProjection.class);
            given(reviewStats.getProductId()).willReturn(1L);
            given(reviewStats.getReviewCount()).willReturn(5L);

            PopularProductProjection orderStats = mock(PopularProductProjection.class);
            given(orderStats.getProductId()).willReturn(1L);
            given(orderStats.getOrderCount()).willReturn(10L);

            given(userProductRepository.searchProducts(any(), any(), any(), any(), any(), any()))
                    .willReturn(new PageImpl<>(List.of(product)));
            given(reviewRepository.findReviewStatsByProductIds(any())).willReturn(List.of(reviewStats));
            given(orderRepository.findOrderCountsByProductIds(any())).willReturn(List.of(orderStats));
            given(productMapper.productToProductDto(product)).willReturn(dto);

            Page<ResponseProductDto> result = userProductService.search(
                    "", null, null, null, null, "latest", pageable);

            assertThat(result.getContent().get(0).getReviewCount()).isEqualTo(5L);
            assertThat(result.getContent().get(0).getOrderCount()).isEqualTo(10L);
        }
    }

    // ────────────────────────────────────────────────
    // 상품 상세 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("상품 상세 조회")
    class GetDetail {

        @Test
        @DisplayName("성공")
        void getDetail_success() {
            Product product = buildProduct(1L);
            ResponseProductDto dto = new ResponseProductDto();

            given(userProductRepository.findByIdWithDetails(1L)).willReturn(Optional.of(product));
            given(productMapper.productToProductDto(product)).willReturn(dto);

            ResponseProductDto result = userProductService.getDetail(1L);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void getDetail_fail_notFound() {
            given(userProductRepository.findByIdWithDetails(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userProductService.getDetail(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_SELECT.getMessage());
        }
    }
}
