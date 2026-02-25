package com.fasthub.backend.admin.order;

import com.fasthub.backend.admin.order.dto.ResponseOrderDto;
import com.fasthub.backend.admin.order.dto.UpdateOrderStatusDto;
import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.mapper.OrderMapper;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.order.service.OrderService;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.usr.entity.User;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    private Order buildOrder() {
        return Order.builder()
                .user(User.builder().build())
                .product(Product.builder()
                        .productNm("에어맥스")
                        .productCode("AM-001")
                        .productPrice(150000)
                        .productQuantity(100)
                        .build())
                .quantity(2)
                .totalPrice(300000)
                .status(OrderStatus.PAYMENT_COMPLETE)
                .shippingAddress("서울시 강남구")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .build();
    }

    // ────────────────────────────────────────────────
    // 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("목록 조회")
    class OrderList {

        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회")
        void list_success_noKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            Order order = buildOrder();
            ResponseOrderDto responseDto = new ResponseOrderDto();
            Page<Order> orderPage = new PageImpl<>(List.of(order));

            given(orderRepository.findAllByKeyword(null, pageable)).willReturn(orderPage);
            given(orderMapper.orderToResponseDto(order)).willReturn(responseDto);

            Page<ResponseOrderDto> result = orderService.list(null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 수령인 이름으로 검색")
        void list_success_withKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            String keyword = "홍길동";
            Order order = buildOrder();
            ResponseOrderDto responseDto = new ResponseOrderDto();
            Page<Order> orderPage = new PageImpl<>(List.of(order));

            given(orderRepository.findAllByKeyword(keyword, pageable)).willReturn(orderPage);
            given(orderMapper.orderToResponseDto(order)).willReturn(responseDto);

            Page<ResponseOrderDto> result = orderService.list(keyword, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    // ────────────────────────────────────────────────
    // 주문 상태 변경
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주문 상태 변경")
    class UpdateStatus {

        @Test
        @DisplayName("성공 - 결제완료 → 배송중")
        void updateStatus_success_toShipping() {
            UpdateOrderStatusDto dto = new UpdateOrderStatusDto();
            dto.setId(1L);
            dto.setStatus(OrderStatus.SHIPPING);

            Order order = buildOrder();
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));

            orderService.updateStatus(dto);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);
        }

        @Test
        @DisplayName("성공 - 배송중 → 배송완료")
        void updateStatus_success_toDelivered() {
            UpdateOrderStatusDto dto = new UpdateOrderStatusDto();
            dto.setId(1L);
            dto.setStatus(OrderStatus.DELIVERED);

            Order order = buildOrder();
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));

            orderService.updateStatus(dto);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주문")
        void updateStatus_fail_orderNotFound() {
            UpdateOrderStatusDto dto = new UpdateOrderStatusDto();
            dto.setId(999L);
            dto.setStatus(OrderStatus.SHIPPING);

            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.updateStatus(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            Order order = buildOrder();

            given(orderRepository.findById(1L)).willReturn(Optional.of(order));

            orderService.delete(1L);

            then(orderRepository).should().delete(order);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주문")
        void delete_fail_orderNotFound() {
            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.delete(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());

            then(orderRepository).should(never()).delete(any());
        }
    }
}
