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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthRepository authRepository;
    private final RedissonClient redissonClient;

    // 주문 준비 - 재고 검증 후 PENDING 주문 생성 (결제 승인 전)
    @Transactional
    public ResponseUserOrderDto order(Long userId, InsertUserOrderDto dto) {
        String lockKey = "product:lock:" + dto.getProductId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException(ErrorCode.ORDER_CONCURRENT_FAIL);
            }

            User user = authRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_PRODUCT_NOT_FOUND));

            // 재고 검증 (차감은 결제 승인 후)
            if (product.getProductQuantity() < dto.getQuantity()) {
                throw new BusinessException(ErrorCode.ORDER_QUANTITY_EXCEEDED);
            }

            // 포인트 잔액 검증 (차감은 결제 승인 후)
            int usePoint = dto.getUsePoint();
            if (usePoint < 0) throw new BusinessException(ErrorCode.POINT_INVALID_AMOUNT);
            if (usePoint > 0 && user.getPoint() < usePoint) throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);

            int totalPrice = product.getProductPrice() * dto.getQuantity();

            Order order = orderRepository.save(Order.builder()
                    .user(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .totalPrice(totalPrice)
                    .usedPoint(usePoint)
                    .status(OrderStatus.PENDING)
                    .tossOrderId(UUID.randomUUID().toString())
                    .shippingAddress(dto.getShippingAddress())
                    .recipientName(dto.getRecipientName())
                    .recipientPhone(dto.getRecipientPhone())
                    .build());

            log.info("[Order] 주문 준비 userId={}, productId={}, tossOrderId={}", userId, dto.getProductId(), order.getTossOrderId());
            return ResponseUserOrderDto.of(order, 0);

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
        return orderRepository.findByUserAndStatusNot(user, OrderStatus.PENDING, pageable)
                .map(o -> ResponseUserOrderDto.of(o, 0));
    }
}
