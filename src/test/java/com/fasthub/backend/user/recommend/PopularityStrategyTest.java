package com.fasthub.backend.user.recommend;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.strategy.PopularProductProjection;
import com.fasthub.backend.user.recommend.strategy.PopularityStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("PopularityStrategy 테스트")
class PopularityStrategyTest {

    @InjectMocks
    private PopularityStrategy popularityStrategy;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;


    // 테스트용 Projection 구현체
    private PopularProductProjection buildProjection(Long productId, Long orderCount) {
        return new PopularProductProjection() {
            @Override public Long getProductId()    { return productId; }
            @Override public Long getOrderCount()   { return orderCount; }
        };
    }

    private Product buildProduct(Long id, String name, ProductCategory category) {
        Brand brand = Brand.builder()
                .brandNm("나이키")
                .build();
        return Product.builder()
                .id(id)
                .productNm(name)
                .productCode("NK-00" + id)
                .productPrice(100000)
                .productQuantity(10)
                .category(category)
                .brand(brand)
                .build();
    }




    // ────────────────────────────────────────────────
    // 30일 인기 상품 정상 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("30일 인기 상품 정상 조회")
    class PopularIn30Days {


        @Test
        @DisplayName("30일 인기 상품이 충분하면 그대로 반환")
        void recommend_returns30DayPopular() {
            int limit = 2;
            List<PopularProductProjection> projections = List.of(
                    buildProjection(1L, 5L),
                    buildProjection(2L, 3L)
            );
            Product product1 = buildProduct(1L, "나이키 에어포스", ProductCategory.SHOES);
            Product product2 = buildProduct(2L, "나이키 후디",    ProductCategory.TOP);

            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(projections);
            given(productRepository.findAllById(List.of(1L, 2L)))
                    .willReturn(List.of(product1, product2));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getOrderCount()).isEqualTo(5L);
            assertThat(result.get(1).getOrderCount()).isEqualTo(3L);
        }

        @Test
        @DisplayName("반환된 DTO의 reason이 '최근 30일 인기 상품'")
        void recommend_30day_reasonText() {
            int limit = 1;
            List<PopularProductProjection> projections = List.of(buildProjection(1L, 5L));
            Product product = buildProduct(1L, "나이키 에어포스", ProductCategory.SHOES);

            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(projections);
            given(productRepository.findAllById(List.of(1L)))
                    .willReturn(List.of(product));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result.get(0).getReason()).isEqualTo("최근 30일 인기 상품");
        }

        @Test
        @DisplayName("인기 순서(orderCount 높은 순)가 유지됨")
        void recommend_maintainsPopularityOrder() {
            int limit = 3;
            // orderCount: 10 > 7 > 2 순서
            List<PopularProductProjection> projections = List.of(
                    buildProjection(1L, 10L),
                    buildProjection(2L, 7L),
                    buildProjection(3L, 2L)
            );
            Product p1 = buildProduct(1L, "상품A", ProductCategory.SHOES);
            Product p2 = buildProduct(2L, "상품B", ProductCategory.TOP);
            Product p3 = buildProduct(3L, "상품C", ProductCategory.BOTTOM);

            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(projections);
            given(productRepository.findAllById(List.of(1L, 2L, 3L)))
                    .willReturn(List.of(p1, p2, p3));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result.get(0).getOrderCount()).isEqualTo(10L);
            assertThat(result.get(1).getOrderCount()).isEqualTo(7L);
            assertThat(result.get(2).getOrderCount()).isEqualTo(2L);
        }
    }

    // ────────────────────────────────────────────────
    // 30일 결과 부족 → 90일 확장
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("30일 결과 부족 → 90일로 확장")
    class FallbackTo90Days {

        @Test
        @DisplayName("30일 결과 없으면 90일 결과 반환")
        void recommend_fallsBackTo90Days_whenNo30DayData() {
            int limit = 2;
            Product product = buildProduct(1L, "나이키 에어포스", ProductCategory.SHOES);

            // 30일 → 결과 없음
            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(Collections.emptyList())               // 1번 호출 (30일)
                    .willReturn(List.of(buildProjection(1L, 2L)));     // 2번 호출 (90일)

            given(productRepository.findAllById(List.of(1L)))
                    .willReturn(List.of(product));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getReason()).isEqualTo("최근 3개월 인기 상품");
        }
    }

    // ────────────────────────────────────────────────
    // 주문 이력 없음 → 최신 상품 반환
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주문 이력 없음 → 최신 상품 반환")
    class FallbackToLatest {

        @Test
        @DisplayName("30일, 90일 모두 결과 없으면 최신 상품 반환")
        void recommend_fallsBackToLatest_whenNoOrderHistory() {
            int limit = 2;
            Product product1 = buildProduct(1L, "최신상품A", ProductCategory.TOP);
            Product product2 = buildProduct(2L, "최신상품B", ProductCategory.OUTER);

            // 30일, 90일 모두 결과 없음
            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(Collections.emptyList());

            // 최신 상품 조회 (findAll with Pageable)
            given(productRepository.findAll(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(product1, product2)));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductNm()).isEqualTo("최신상품A");
        }

        @Test
        @DisplayName("최신 상품 fallback 시 orderCount는 0")
        void recommend_latestFallback_orderCountIsZero() {
            int limit = 1;
            Product product = buildProduct(1L, "최신상품A", ProductCategory.TOP);

            given(orderRepository.findPopularProductIds(any(), any(Pageable.class)))
                    .willReturn(Collections.emptyList());
            given(productRepository.findAll(any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of(product)));

            List<RecommendProductDto> result = popularityStrategy.recommend(limit);

            assertThat(result.get(0).getOrderCount()).isEqualTo(0L);
        }
    }
}
