package com.fasthub.backend.user.order;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.order.dto.InsertUserOrderDto;
import com.fasthub.backend.user.order.dto.ResponseUserOrderDto;
import com.fasthub.backend.user.order.service.UserOrderService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserOrderService 테스트")
class UserOrderServiceTest {

    @InjectMocks
    private UserOrderService userOrderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser(int point) {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .point(point)
                .build();
    }

    private Product buildProduct(int quantity, int price) {
        return Product.builder()
                .id(10L)
                .productNm("테스트 상품")
                .productCode("P001")
                .productPrice(price)
                .productQuantity(quantity)
                .category(ProductCategory.TOP)
                .build();
    }

    private Order buildOrder(User user, Product product) {
        return Order.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(2)
                .totalPrice(product.getProductPrice() * 2)
                .usedPoint(0)
                .status(OrderStatus.PENDING)
                .shippingAddress("서울시 강남구")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .tossOrderId("toss-uuid-001")
                .build();
    }

    // ────────────────────────────────────────────────
    // 주문 생성
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주문 생성")
    class OrderCreate {

        @Test
        @DisplayName("성공 - 포인트 미사용")
        void order_success_noPoint() throws InterruptedException {
            User user = buildUser(0);
            Product product = buildProduct(10, 50000);
            Order order = buildOrder(user, product);

            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);
            given(dto.getQuantity()).willReturn(2);
            given(dto.getUsePoint()).willReturn(0);
            given(dto.getShippingAddress()).willReturn("서울시 강남구");
            given(dto.getRecipientName()).willReturn("홍길동");
            given(dto.getRecipientPhone()).willReturn("010-1234-5678");

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(orderRepository.save(any(Order.class))).willReturn(order);

            ResponseUserOrderDto result = userOrderService.order(1L, dto);

            assertThat(result.getProductNm()).isEqualTo("테스트 상품");
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            then(orderRepository).should().save(any(Order.class));
        }

        @Test
        @DisplayName("성공 - 포인트 사용")
        void order_success_withPoint() throws InterruptedException {
            User user = buildUser(10000);
            Product product = buildProduct(10, 50000);
            Order order = buildOrder(user, product);

            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);
            given(dto.getQuantity()).willReturn(1);
            given(dto.getUsePoint()).willReturn(5000);
            given(dto.getShippingAddress()).willReturn("서울시 강남구");
            given(dto.getRecipientName()).willReturn("홍길동");
            given(dto.getRecipientPhone()).willReturn("010-1234-5678");

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(orderRepository.save(any(Order.class))).willReturn(order);

            ResponseUserOrderDto result = userOrderService.order(1L, dto);

            assertThat(result).isNotNull();
            then(orderRepository).should().save(any(Order.class));
        }

        @Test
        @DisplayName("실패 - 락 획득 실패 (동시 주문)")
        void order_fail_lockFail() throws InterruptedException {
            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(false);
            given(rLock.isHeldByCurrentThread()).willReturn(false);

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_CONCURRENT_FAIL.getMessage());

            then(orderRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void order_fail_userNotFound() throws InterruptedException {
            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void order_fail_productNotFound() throws InterruptedException {
            User user = buildUser(0);
            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(999L);

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 주문 수량이 재고 초과")
        void order_fail_quantityExceeded() throws InterruptedException {
            User user = buildUser(0);
            Product product = buildProduct(3, 50000); // 재고 3개

            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);
            given(dto.getQuantity()).willReturn(5); // 5개 주문 시도

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_QUANTITY_EXCEEDED.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 포인트 금액 (음수)")
        void order_fail_invalidPointAmount() throws InterruptedException {
            User user = buildUser(5000);
            Product product = buildProduct(10, 50000);

            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);
            given(dto.getQuantity()).willReturn(1);
            given(dto.getUsePoint()).willReturn(-1000); // 음수 포인트

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POINT_INVALID_AMOUNT.getMessage());
        }

        @Test
        @DisplayName("실패 - 포인트 잔액 부족")
        void order_fail_pointNotEnough() throws InterruptedException {
            User user = buildUser(1000); // 포인트 1000
            Product product = buildProduct(10, 50000);

            InsertUserOrderDto dto = mock(InsertUserOrderDto.class);
            given(dto.getProductId()).willReturn(10L);
            given(dto.getQuantity()).willReturn(1);
            given(dto.getUsePoint()).willReturn(5000); // 5000 포인트 사용 시도

            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(productRepository.findById(10L)).willReturn(Optional.of(product));

            assertThatThrownBy(() -> userOrderService.order(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POINT_NOT_ENOUGH.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 내 주문 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("내 주문 목록 조회")
    class MyOrders {

        @Test
        @DisplayName("성공")
        void myOrders_success() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser(0);
            Product product = buildProduct(10, 50000);
            Order order = buildOrder(user, product);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findByUserAndStatusNot(user, OrderStatus.PENDING, pageable))
                    .willReturn(new PageImpl<>(List.of(order)));

            Page<ResponseUserOrderDto> result = userOrderService.myOrders(1L, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getProductNm()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("성공 - 주문 없음")
        void myOrders_success_empty() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser(0);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findByUserAndStatusNot(user, OrderStatus.PENDING, pageable))
                    .willReturn(new PageImpl<>(List.of()));

            Page<ResponseUserOrderDto> result = userOrderService.myOrders(1L, pageable);

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void myOrders_fail_userNotFound() {
            Pageable pageable = PageRequest.of(0, 10);

            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userOrderService.myOrders(999L, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}
