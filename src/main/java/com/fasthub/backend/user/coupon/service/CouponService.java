package com.fasthub.backend.user.coupon.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.coupon.dto.CouponCreateDto;
import com.fasthub.backend.user.coupon.dto.CouponDto;
import com.fasthub.backend.user.coupon.dto.CouponUpdateDto;
import com.fasthub.backend.user.coupon.dto.UserCouponDto;
import com.fasthub.backend.user.coupon.entity.Coupon;
import com.fasthub.backend.user.coupon.entity.UserCoupon;
import com.fasthub.backend.user.coupon.repository.CouponRepository;
import com.fasthub.backend.user.coupon.repository.UserCouponRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final AuthRepository authRepository;
    private final RedissonClient redissonClient;

    // ───────────────────────────────────────────────
    // 관리자: 쿠폰 생성
    // ───────────────────────────────────────────────
    @Transactional
    public void createCoupon(CouponCreateDto dto) {
        if (couponRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(ErrorCode.COUPON_CODE_DUPLICATE);
        }
        couponRepository.save(Coupon.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minOrderPrice(dto.getMinOrderPrice())
                .maxDiscountPrice(dto.getMaxDiscountPrice())
                .totalQuantity(dto.getTotalQuantity())
                .issuedCount(0)
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build());
        log.info("[Coupon] 쿠폰 생성 code={}", dto.getCode());
    }

    // ───────────────────────────────────────────────
    // 관리자: 전체 쿠폰 목록 조회
    // ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponDto::of)
                .collect(Collectors.toList());
    }

    // ───────────────────────────────────────────────
    // 관리자: 쿠폰 수정 (코드 제외)
    // ───────────────────────────────────────────────
    @Transactional
    public void updateCoupon(Long id, CouponUpdateDto dto) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        coupon.update(dto.getName(), dto.getDiscountType(), dto.getDiscountValue(),
                dto.getMinOrderPrice(), dto.getMaxDiscountPrice(), dto.getTotalQuantity(),
                dto.getStartAt(), dto.getEndAt());
        log.info("[Coupon] 쿠폰 수정 id={}", id);
    }

    // ───────────────────────────────────────────────
    // 관리자: 쿠폰 삭제 (미발급 쿠폰만 삭제 가능)
    // ───────────────────────────────────────────────
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        if (coupon.getIssuedCount() > 0) {
            throw new BusinessException(ErrorCode.COUPON_HAS_ISSUED);
        }
        couponRepository.delete(coupon);
        log.info("[Coupon] 쿠폰 삭제 id={}", id);
    }

    // ───────────────────────────────────────────────
    // 사용자: 쿠폰 코드 입력으로 발급 (Redis 분산락)
    //  - 선착순 쿠폰 동시 요청 시 초과 발급 방지
    // ───────────────────────────────────────────────
    @Transactional
    public void issueCoupon(Long userId, String code) {
        RLock lock = redissonClient.getLock("coupon:lock:" + code);
        try {
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) throw new BusinessException(ErrorCode.COUPON_CONCURRENT_FAIL);

            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

            if (!coupon.isAvailable()) {
                throw new BusinessException(ErrorCode.COUPON_NOT_AVAILABLE);
            }

            // 애플리케이션 레벨 중복 체크 (DB 유니크 제약과 이중 방어)
            if (userCouponRepository.existsByUserIdAndCouponId(userId, coupon.getId())) {
                throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
            }

            User user = authRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            coupon.increaseIssuedCount();
            userCouponRepository.save(UserCoupon.builder()
                    .user(user)
                    .coupon(coupon)
                    .isUsed(false)
                    .expiredAt(coupon.getEndAt())
                    .build());

            log.info("[Coupon] 쿠폰 발급 userId={}, code={}", userId, code);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.COUPON_CONCURRENT_FAIL);
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }

    // ───────────────────────────────────────────────
    // 사용자: 보유 쿠폰 목록 조회 (미사용만)
    // ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserCouponDto> getMyCoupons(Long userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userCouponRepository.findAvailableByUser(user).stream()
                .map(UserCouponDto::of)
                .collect(Collectors.toList());
    }

    // ───────────────────────────────────────────────
    // 주문 시 쿠폰 유효성 검증 → 할인 금액 반환
    //  - UserOrderService에서 호출
    // ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public int validateAndGetDiscount(Long userId, Long userCouponId, int totalPrice) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if (!userCoupon.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.COUPON_NOT_OWNER);
        }
        if (userCoupon.isUsed()) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_USED);
        }
        if (userCoupon.isExpired()) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        Coupon coupon = userCoupon.getCoupon();
        if (totalPrice < coupon.getMinOrderPrice()) {
            throw new BusinessException(ErrorCode.COUPON_MIN_ORDER_NOT_MET);
        }

        return coupon.calculateDiscount(totalPrice);
    }
}
