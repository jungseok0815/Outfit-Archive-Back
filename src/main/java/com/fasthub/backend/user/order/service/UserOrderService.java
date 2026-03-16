package com.fasthub.backend.user.order.service;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.order.dto.InsertUserOrderDto;
import com.fasthub.backend.user.order.dto.ResponseUserOrderDto;
import com.fasthub.backend.user.point.entity.PointHistory;
import com.fasthub.backend.user.point.entity.PointHistory.PointType;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrderService {

    private static final double POINT_EARN_RATE = 0.01; // 결제 금액의 1% 적립

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthRepository authRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public ResponseUserOrderDto order(Long userId, InsertUserOrderDto dto) {
        String lockKey = "product:lock:" + dto.getProductId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 최대 5초 대기, 락 획득 후 3초 내 자동 해제
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);
            }

            User user = authRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_PRODUCT_NOT_FOUND));

            // 재고 확인 및 차감
            if (product.getProductQuantity() < dto.getQuantity()) {
                throw new BusinessException(ErrorCode.ORDER_QUANTITY_EXCEEDED);
            }
            product.decreaseQuantity(dto.getQuantity());

            // 포인트 사용 처리
            int usePoint = dto.getUsePoint();
            if (usePoint < 0) {
                throw new BusinessException(ErrorCode.POINT_INVALID_AMOUNT);
            }
            if (usePoint > 0) {
                if (user.getPoint() < usePoint) {
                    throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);
                }
                user.usePoint(usePoint);
                pointHistoryRepository.save(PointHistory.builder()
                        .user(user)
                        .amount(-usePoint)
                        .balanceAfter(user.getPoint())
                        .description("포인트 사용 - " + product.getProductNm())
                        .type(PointType.USE)
                        .build());
            }

            int totalPrice = product.getProductPrice() * dto.getQuantity();
            int actualPayment = totalPrice - usePoint;

            // 포인트 적립 (실제 결제 금액 기준 1%)
            int earnPoint = (int) (actualPayment * POINT_EARN_RATE);
            user.earnPoint(earnPoint);
            pointHistoryRepository.save(PointHistory.builder()
                    .user(user)
                    .amount(earnPoint)
                    .balanceAfter(user.getPoint())
                    .description("구매 적립 - " + product.getProductNm())
                    .type(PointType.EARN)
                    .build());

            // 주문 생성
            Order order = orderRepository.save(Order.builder()
                    .user(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .totalPrice(totalPrice)
                    .usedPoint(usePoint)
                    .status(OrderStatus.PENDING)
                    .shippingAddress(dto.getShippingAddress())
                    .recipientName(dto.getRecipientName())
                    .recipientPhone(dto.getRecipientPhone())
                    .build());

            log.info("[Order] 주문 완료 userId={}, productId={}, usePoint={}, earnPoint={}",
                    userId, dto.getProductId(), usePoint, earnPoint);

            return ResponseUserOrderDto.of(order, earnPoint);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<ResponseUserOrderDto> myOrders(Long userId, Pageable pageable) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return orderRepository.findByUser(user, pageable)
                .map(o -> ResponseUserOrderDto.of(o, 0));
    }
}
