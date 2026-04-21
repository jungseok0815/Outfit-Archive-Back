package com.fasthub.backend.user.payment;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.coupon.repository.UserCouponRepository;
import com.fasthub.backend.user.payment.client.TossPaymentClient;
import com.fasthub.backend.user.payment.dto.PaymentConfirmRequestDto;
import com.fasthub.backend.user.payment.service.PaymentService;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
import com.fasthub.backend.user.usr.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentService 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductSizeRepository productSizeRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private TossPaymentClient tossPaymentClient;

    @Mock
    private RedissonClient redissonClient;

    private RLock buildLock(boolean acquired) throws InterruptedException {
        RLock lock = mock(RLock.class);
        given(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(acquired);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        return lock;
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .point(0)
                .build();
    }

    private Order buildOrder(OrderStatus status, int totalPrice, int usedPoint, int couponDiscount) {
        User user = buildUser();
        Product product = Product.builder()
                .id(1L)
                .productNm("테스트 상품")
                .productCode("P001")
                .productPrice(totalPrice)
                .productQuantity(100)
                .category(null)
                .build();

        Order order = mock(Order.class);
        given(order.getStatus()).willReturn(status);
        given(order.getTotalPrice()).willReturn(totalPrice);
        given(order.getUsedPoint()).willReturn(usedPoint);
        given(order.getCouponDiscount()).willReturn(couponDiscount);
        given(order.getProduct()).willReturn(product);
        given(order.getUser()).willReturn(user);
        given(order.getUserCouponId()).willReturn(null);
        given(order.getSizeNm()).willReturn(null);
        return order;
    }

    // ────────────────────────────────────────────────
    // 결제 승인
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("결제 승인")
    class Confirm {

        private PaymentConfirmRequestDto buildDto(String paymentKey, String orderId, int amount) {
            PaymentConfirmRequestDto dto = mock(PaymentConfirmRequestDto.class);
            given(dto.getPaymentKey()).willReturn(paymentKey);
            given(dto.getOrderId()).willReturn(orderId);
            given(dto.getAmount()).willReturn(amount);
            return dto;
        }

        @Test
        @DisplayName("성공")
        void confirm_success() throws InterruptedException {
            RLock lock = buildLock(true);
            Order order = buildOrder(OrderStatus.PENDING, 50000, 0, 0);
            PaymentConfirmRequestDto dto = buildDto("payKey", "tossOrder01", 50000);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(orderRepository.findByTossOrderId("tossOrder01")).willReturn(Optional.of(order));

            paymentService.confirm(dto);

            then(tossPaymentClient).should().confirm("payKey", "tossOrder01", 50000);
            then(pointHistoryRepository).should(atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("실패 - 락 획득 실패")
        void confirm_fail_lockFail() throws InterruptedException {
            RLock lock = buildLock(false);
            PaymentConfirmRequestDto dto = buildDto("payKey", "tossOrder01", 50000);

            given(redissonClient.getLock(anyString())).willReturn(lock);

            assertThatThrownBy(() -> paymentService.confirm(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_CONCURRENT_FAIL.getMessage());
        }

        @Test
        @DisplayName("실패 - 이미 결제된 주문")
        void confirm_fail_alreadyPaid() throws InterruptedException {
            RLock lock = buildLock(true);
            Order order = buildOrder(OrderStatus.PAYMENT_COMPLETE, 50000, 0, 0);
            PaymentConfirmRequestDto dto = buildDto("payKey", "tossOrder01", 50000);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(orderRepository.findByTossOrderId("tossOrder01")).willReturn(Optional.of(order));

            assertThatThrownBy(() -> paymentService.confirm(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_ALREADY_PAID.getMessage());
        }

        @Test
        @DisplayName("실패 - 금액 변조")
        void confirm_fail_amountMismatch() throws InterruptedException {
            RLock lock = buildLock(true);
            Order order = buildOrder(OrderStatus.PENDING, 50000, 0, 0);
            PaymentConfirmRequestDto dto = buildDto("payKey", "tossOrder01", 40000);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(orderRepository.findByTossOrderId("tossOrder01")).willReturn(Optional.of(order));

            assertThatThrownBy(() -> paymentService.confirm(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PAYMENT_AMOUNT_MISMATCH.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 결제 취소
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("결제 취소")
    class Cancel {

        @Test
        @DisplayName("성공 - PENDING 상태 취소")
        void cancel_success_pending() {
            Order order = buildOrder(OrderStatus.PENDING, 50000, 0, 0);

            given(orderRepository.findByTossOrderId("tossOrder01")).willReturn(Optional.of(order));

            paymentService.cancel("tossOrder01", "단순 변심");

            then(order).should().cancel();
            then(tossPaymentClient).should(never()).cancel(any(), any());
        }

        @Test
        @DisplayName("실패 - 취소 불가 상태")
        void cancel_fail_notCancelable() {
            Order order = buildOrder(OrderStatus.SHIPPING, 50000, 0, 0);

            given(orderRepository.findByTossOrderId("tossOrder01")).willReturn(Optional.of(order));

            assertThatThrownBy(() -> paymentService.cancel("tossOrder01", "단순 변심"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PAYMENT_NOT_CANCELABLE.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주문")
        void cancel_fail_notFound() {
            given(orderRepository.findByTossOrderId("notExist")).willReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.cancel("notExist", "단순 변심"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }
}
