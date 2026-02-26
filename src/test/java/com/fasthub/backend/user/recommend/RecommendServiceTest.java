package com.fasthub.backend.user.recommend;

import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.recommend.dto.RecommendProductDto;
import com.fasthub.backend.user.recommend.service.RecommendService;
import com.fasthub.backend.user.recommend.strategy.PopularityStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendService 테스트")
class RecommendServiceTest {

    @InjectMocks
    private RecommendService recommendService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PopularityStrategy popularityStrategy;

    private List<RecommendProductDto> buildDummyRecommendList() {
        return List.of(
                RecommendProductDto.builder()
                        .productId(1L)
                        .productNm("나이키 에어포스 1")
                        .productPrice(119000)
                        .category(ProductCategory.SHOES)
                        .brandNm("나이키")
                        .orderCount(5)
                        .reason("최근 30일 인기 상품")
                        .build(),
                RecommendProductDto.builder()
                        .productId(2L)
                        .productNm("아디다스 트레이닝 팬츠")
                        .productPrice(89000)
                        .category(ProductCategory.BOTTOM)
                        .brandNm("아디다스")
                        .orderCount(3)
                        .reason("최근 30일 인기 상품")
                        .build()
        );
    }

    // ────────────────────────────────────────────────
    // Cold Start (구매 이력 없는 신규 유저)
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("Cold Start - 구매 이력 없는 신규 유저")
    class ColdStart {

        @Test
        @DisplayName("구매 이력 0건 → PopularityStrategy 호출")
        void recommend_coldStart_callsPopularityStrategy() {
            Long userId = 1L;
            int limit = 10;
            given(orderRepository.countByUserId(userId)).willReturn(0L);
            given(popularityStrategy.recommend(limit)).willReturn(buildDummyRecommendList());

            List<RecommendProductDto> result = recommendService.recommend(userId, limit);

            then(popularityStrategy).should().recommend(limit);
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("구매 이력 0건 → 반환 결과에 인기 상품 포함")
        void recommend_coldStart_returnsPopularProducts() {
            Long userId = 1L;
            int limit = 10;
            given(orderRepository.countByUserId(userId)).willReturn(0L);
            given(popularityStrategy.recommend(limit)).willReturn(buildDummyRecommendList());

            List<RecommendProductDto> result = recommendService.recommend(userId, limit);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductNm()).isEqualTo("나이키 에어포스 1");
            assertThat(result.get(0).getReason()).isEqualTo("최근 30일 인기 상품");
        }

        @Test
        @DisplayName("구매 이력 0건 → limit 파라미터 그대로 전달")
        void recommend_coldStart_passesLimitCorrectly() {
            Long userId = 1L;
            int limit = 5;
            given(orderRepository.countByUserId(userId)).willReturn(0L);
            given(popularityStrategy.recommend(5)).willReturn(buildDummyRecommendList().subList(0, 1));

            recommendService.recommend(userId, limit);

            then(popularityStrategy).should().recommend(5);
            then(popularityStrategy).should(never()).recommend(10);
        }
    }

    // ────────────────────────────────────────────────
    // 기존 유저 (구매 이력 있음)
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("기존 유저 - 구매 이력 있음")
    class ExistingUser {

        @Test
        @DisplayName("구매 이력 1건 이상 → PopularityStrategy 임시 호출")
        void recommend_existingUser_callsPopularityStrategy() {
            Long userId = 2L;
            int limit = 10;
            given(orderRepository.countByUserId(userId)).willReturn(3L);
            given(popularityStrategy.recommend(limit)).willReturn(buildDummyRecommendList());

            List<RecommendProductDto> result = recommendService.recommend(userId, limit);

            then(popularityStrategy).should().recommend(limit);
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("구매 이력 많아도 결과 반환됨")
        void recommend_existingUser_returnsResult() {
            Long userId = 2L;
            int limit = 10;
            given(orderRepository.countByUserId(userId)).willReturn(20L);
            given(popularityStrategy.recommend(limit)).willReturn(buildDummyRecommendList());

            List<RecommendProductDto> result = recommendService.recommend(userId, limit);

            assertThat(result).isNotNull();
            assertThat(result).isNotEmpty();
        }
    }
}
