package com.fasthub.backend.user.similar;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.similar.dto.SimilarProductDto;
import com.fasthub.backend.user.similar.service.SimilarProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimilarProductService 테스트")
class SimilarProductServiceTest {

    @InjectMocks
    private SimilarProductService similarProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    private Product buildProductWithEmbedding(Long id, String embedding) {
        return Product.builder()
                .id(id)
                .productNm("상품" + id)
                .productCode("P00" + id)
                .productPrice(50000)
                .productQuantity(100)
                .category(ProductCategory.TOP)
                .embedding(embedding)
                .build();
    }

    // ────────────────────────────────────────────────
    // 유사 상품 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("유사 상품 조회")
    class FindSimilar {

        @Test
        @DisplayName("성공 - 유사 상품 반환")
        void findSimilar_success() throws Exception {
            float[] vec1 = {1.0f, 0.0f};
            float[] vec2 = {0.9f, 0.1f};

            Product target = buildProductWithEmbedding(1L, "[1.0, 0.0]");
            Product other = buildProductWithEmbedding(2L, "[0.9, 0.1]");

            given(productRepository.findById(1L)).willReturn(Optional.of(target));
            given(productRepository.findAll()).willReturn(List.of(target, other));
            given(objectMapper.readValue("[1.0, 0.0]", float[].class)).willReturn(vec1);
            given(objectMapper.readValue("[0.9, 0.1]", float[].class)).willReturn(vec2);

            List<SimilarProductDto> result = similarProductService.findSimilar(1L, 5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void findSimilar_fail_notFound() {
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> similarProductService.findSimilar(999L, 5))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_SELECT.getMessage());
        }

        @Test
        @DisplayName("빈 결과 - 임베딩 없는 상품")
        void findSimilar_empty_noEmbedding() {
            Product target = buildProductWithEmbedding(1L, null);

            given(productRepository.findById(1L)).willReturn(Optional.of(target));

            List<SimilarProductDto> result = similarProductService.findSimilar(1L, 5);

            assertThat(result).isEmpty();
        }
    }
}
