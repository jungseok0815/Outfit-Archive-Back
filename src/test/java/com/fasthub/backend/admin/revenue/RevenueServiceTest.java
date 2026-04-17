package com.fasthub.backend.admin.revenue;

import com.fasthub.backend.admin.order.dto.RevenueByBrandDto;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.revenue.service.RevenueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevenueService 테스트")
class RevenueServiceTest {

    @InjectMocks
    private RevenueService revenueService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("성공 - 브랜드별 매출 조회")
    void getRevenue_success() {
        Long brandId = 1L;
        RevenueByBrandDto dto = new RevenueByBrandDto(1L, "나이키", 10L, 1000000L);
        given(orderRepository.findRevenueByBrand(brandId)).willReturn(List.of(dto));

        List<RevenueByBrandDto> result = revenueService.getRevenue(brandId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrandNm()).isEqualTo("나이키");
    }

    @Test
    @DisplayName("성공 - 결과 없음")
    void getRevenue_empty() {
        given(orderRepository.findRevenueByBrand(null)).willReturn(List.of());

        List<RevenueByBrandDto> result = revenueService.getRevenue(null);

        assertThat(result).isEmpty();
    }
}
