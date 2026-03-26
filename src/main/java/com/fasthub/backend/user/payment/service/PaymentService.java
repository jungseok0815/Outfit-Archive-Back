package com.fasthub.backend.user.payment.service;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.order.dto.ResponseUserOrderDto;
import com.fasthub.backend.user.payment.client.TossPaymentClient;
import com.fasthub.backend.user.payment.dto.PaymentConfirmRequestDto;
import com.fasthub.backend.user.point.entity.PointHistory;
import com.fasthub.backend.user.point.entity.PointHistory.PointType;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
import com.fasthub.backend.user.usr.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private static final double POINT_EARN_RATE = 0.01; // 결제 금액의 1% 적립

    private final OrderRepository orderRepository;
    private final ProductSizeRepository productSizeRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final TossPaymentClient tossPaymentClient;
    private final RedissonClient redissonClient;

    // 결제 승인 - 토스 승인 후 재고 차감, 포인트 처리
    @Transactional
    public void confirm(PaymentConfirmRequestDto dto) {
        String lockKey = "payment:lock:" + dto.getOrderId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);

            Order order = orderRepository.findByTossOrderId(dto.getOrderId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

            // 이미 결제된 주문 중복 방지
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
            }

            // 금액 변조 검증 (totalPrice - usedPoint = 실제 결제 금액)
            int expectedAmount = order.getTotalPrice() - order.getUsedPoint();
            if (expectedAmount != dto.getAmount()) {
                throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            // 토스 결제 승인 API 호출
            tossPaymentClient.confirm(dto.getPaymentKey(), dto.getOrderId(), dto.getAmount());

            // 재고 차감 (사이즈 있으면 사이즈 재고도 차감)
            order.getProduct().decreaseQuantity(order.getQuantity());
            if (order.getSizeNm() != null) {
                productSizeRepository.findByProductAndSizeNm(order.getProduct(), order.getSizeNm())
                        .ifPresent(s -> s.decreaseQuantity(order.getQuantity()));
            }

            // 포인트 차감
            User user = order.getUser();
            int usePoint = order.getUsedPoint();
            if (usePoint > 0) {
                user.usePoint(usePoint);
                pointHistoryRepository.save(PointHistory.builder()
                        .user(user)
                        .amount(-usePoint)
                        .balanceAfter(user.getPoint())
                        .description("포인트 사용 - " + order.getProduct().getProductNm())
                        .type(PointType.USE)
                        .build());
            }

            // 포인트 적립 (실제 결제 금액의 1%)
            int earnPoint = (int) (dto.getAmount() * POINT_EARN_RATE);
            user.earnPoint(earnPoint);
            pointHistoryRepository.save(PointHistory.builder()
                    .user(user)
                    .amount(earnPoint)
                    .balanceAfter(user.getPoint())
                    .description("구매 적립 - " + order.getProduct().getProductNm())
                    .type(PointType.EARN)
                    .build());

            // 주문 상태 업데이트
            order.confirmPayment(dto.getPaymentKey());

            log.info("[Payment] 결제 완료 orderId={}, amount={}, earnPoint={}", dto.getOrderId(), dto.getAmount(), earnPoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // PG 없이 직접 결제 완료 처리 (토스 연동 없이 서버에 내역 저장)
    @Transactional
    public ResponseUserOrderDto directComplete(String tossOrderId) {
        String lockKey = "payment:lock:" + tossOrderId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);

            Order order = orderRepository.findByTossOrderId(tossOrderId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
            }

            // 재고 차감 (사이즈 있으면 사이즈 재고도 차감)
            order.getProduct().decreaseQuantity(order.getQuantity());
            if (order.getSizeNm() != null) {
                productSizeRepository.findByProductAndSizeNm(order.getProduct(), order.getSizeNm())
                        .ifPresent(s -> s.decreaseQuantity(order.getQuantity()));
            }

            // 포인트 차감
            User user = order.getUser();
            int usePoint = order.getUsedPoint();
            if (usePoint > 0) {
                user.usePoint(usePoint);
                pointHistoryRepository.save(PointHistory.builder()
                        .user(user)
                        .amount(-usePoint)
                        .balanceAfter(user.getPoint())
                        .description("포인트 사용 - " + order.getProduct().getProductNm())
                        .type(PointType.USE)
                        .build());
            }

            // 포인트 적립 (실제 결제 금액의 1%)
            int actualPayment = order.getTotalPrice() - order.getUsedPoint();
            int earnPoint = (int) (actualPayment * POINT_EARN_RATE);
            user.earnPoint(earnPoint);
            pointHistoryRepository.save(PointHistory.builder()
                    .user(user)
                    .amount(earnPoint)
                    .balanceAfter(user.getPoint())
                    .description("구매 적립 - " + order.getProduct().getProductNm())
                    .type(PointType.EARN)
                    .build());

            // 주문 상태 업데이트 (PG 키 없이 DIRECT로 표시)
            order.confirmPayment("DIRECT");

            log.info("[Payment] 직접 결제 완료 tossOrderId={}, amount={}, earnPoint={}", tossOrderId, actualPayment, earnPoint);
            return ResponseUserOrderDto.of(order, earnPoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // 결제 취소
    @Transactional
    public void cancel(String tossOrderId, String cancelReason) {
        Order order = orderRepository.findByTossOrderId(tossOrderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // PENDING(미결제) 또는 PAYMENT_COMPLETE(결제완료)만 취소 가능
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAYMENT_COMPLETE) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_CANCELABLE);
        }

        // 결제 완료 상태면 토스 취소 API 호출 + 재고/포인트 복원
        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETE) {
            tossPaymentClient.cancel(order.getPaymentKey(), cancelReason);

            // 재고 복원 (사이즈 있으면 사이즈 재고도 복원)
            order.getProduct().increaseQuantity(order.getQuantity());
            if (order.getSizeNm() != null) {
                productSizeRepository.findByProductAndSizeNm(order.getProduct(), order.getSizeNm())
                        .ifPresent(s -> s.increaseQuantity(order.getQuantity()));
            }

            // 포인트 복원
            User user = order.getUser();
            if (order.getUsedPoint() > 0) {
                user.earnPoint(order.getUsedPoint());
                pointHistoryRepository.save(PointHistory.builder()
                        .user(user)
                        .amount(order.getUsedPoint())
                        .balanceAfter(user.getPoint())
                        .description("주문 취소 포인트 환불 - " + order.getProduct().getProductNm())
                        .type(PointType.EARN)
                        .build());
            }
        }

        order.cancel();
        log.info("[Payment] 주문 취소 orderId={}", tossOrderId);
    }
}
