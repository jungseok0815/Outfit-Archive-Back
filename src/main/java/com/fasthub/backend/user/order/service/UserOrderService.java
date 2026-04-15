package com.fasthub.backend.user.order.service;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.coupon.service.CouponService;
import com.fasthub.backend.user.order.dto.InsertUserOrderDto;
import com.fasthub.backend.user.order.dto.ResponseUserOrderDto;
import com.fasthub.backend.user.review.repository.ReviewRepository;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final AuthRepository authRepository;
    private final CouponService couponService;
    private final RedissonClient redissonClient;
    private final ReviewRepository reviewRepository;

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
            boolean hasSizes = !product.getSizes().isEmpty();
            if (hasSizes) {
                if (dto.getSizeNm() == null || dto.getSizeNm().isBlank()) {
                    throw new BusinessException(ErrorCode.ORDER_SIZE_REQUIRED);
                }
                ProductSize size = productSizeRepository.findByProductAndSizeNm(product, dto.getSizeNm())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_SIZE_NOT_FOUND));
                if (size.getQuantity() < dto.getQuantity()) {
                    throw new BusinessException(ErrorCode.ORDER_QUANTITY_EXCEEDED);
                }
            } else {
                if (product.getProductQuantity() < dto.getQuantity()) {
                    throw new BusinessException(ErrorCode.ORDER_QUANTITY_EXCEEDED);
                }
            }

            // 포인트 잔액 검증 (차감은 결제 승인 후)
            int usePoint = dto.getUsePoint();
            if (usePoint < 0) throw new BusinessException(ErrorCode.POINT_INVALID_AMOUNT);
            if (usePoint > 0 && user.getPoint() < usePoint) throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);

            int totalPrice = product.getProductPrice() * dto.getQuantity();

            // 쿠폰 할인 계산 (카테고리/브랜드 적용 대상 검증 포함)
            int couponDiscount = 0;
            if (dto.getUserCouponId() != null) {
                couponDiscount = couponService.validateAndGetDiscount(userId, dto.getUserCouponId(), totalPrice, product);
            }

            // 포인트는 쿠폰 할인 후 금액을 초과할 수 없음
            if (usePoint > totalPrice - couponDiscount) {
                throw new BusinessException(ErrorCode.POINT_INVALID_AMOUNT);
            }

            Order order = orderRepository.save(Order.builder()
                    .user(user)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .totalPrice(totalPrice)
                    .usedPoint(usePoint)
                    .couponDiscount(couponDiscount)
                    .userCouponId(dto.getUserCouponId())
                    .status(OrderStatus.PENDING)
                    .tossOrderId(UUID.randomUUID().toString())
                    .shippingAddress(dto.getShippingAddress())
                    .recipientName(dto.getRecipientName())
                    .recipientPhone(dto.getRecipientPhone())
                    .sizeNm(dto.getSizeNm())
                    .build());

            log.info("[Order] 주문 준비 userId={}, productId={}, tossOrderId={}", userId, dto.getProductId(), order.getTossOrderId());
            return ResponseUserOrderDto.of(order);

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
        Page<Order> orders = orderRepository.findByUserAndStatusNot(user, OrderStatus.PENDING, pageable);

        // 리뷰 작성 여부를 한 번에 조회 (N+1 방지)
        List<Long> orderIds = orders.getContent().stream().map(Order::getId).toList();
        java.util.Set<Long> reviewedOrderIds = new java.util.HashSet<>(
                orderIds.isEmpty() ? java.util.List.of() : reviewRepository.findReviewedOrderIds(orderIds)
        );

        return orders.map(o -> ResponseUserOrderDto.of(o, reviewedOrderIds.contains(o.getId())));
    }
}
