package com.fasthub.backend.user.coupon;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.coupon.dto.CouponCreateDto;
import com.fasthub.backend.user.coupon.entity.Coupon;
import com.fasthub.backend.user.coupon.entity.UserCoupon;
import com.fasthub.backend.user.coupon.repository.CouponRepository;
import com.fasthub.backend.user.coupon.repository.UserCouponRepository;
import com.fasthub.backend.user.coupon.service.CouponService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CouponService 테스트")
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private RedissonClient redissonClient;

    private User buildUser() {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    private Coupon buildCoupon(boolean available) {
        Coupon coupon = mock(Coupon.class);
        given(coupon.getId()).willReturn(1L);
        given(coupon.isAvailable()).willReturn(available);
        given(coupon.getEndAt()).willReturn(LocalDateTime.now().plusDays(7));
        return coupon;
    }

    private RLock buildLock(boolean acquired) throws InterruptedException {
        RLock lock = mock(RLock.class);
        given(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).willReturn(acquired);
        given(lock.isHeldByCurrentThread()).willReturn(true);
        return lock;
    }

    // ────────────────────────────────────────────────
    // 쿠폰 생성
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("쿠폰 생성")
    class CreateCoupon {

        @Test
        @DisplayName("성공")
        void createCoupon_success() {
            CouponCreateDto dto = mock(CouponCreateDto.class);
            given(dto.getCode()).willReturn("SAVE10");

            given(couponRepository.existsByCode("SAVE10")).willReturn(false);

            couponService.createCoupon(dto);

            then(couponRepository).should().save(any(Coupon.class));
        }

        @Test
        @DisplayName("실패 - 중복 코드")
        void createCoupon_fail_duplicate() {
            CouponCreateDto dto = mock(CouponCreateDto.class);
            given(dto.getCode()).willReturn("SAVE10");

            given(couponRepository.existsByCode("SAVE10")).willReturn(true);

            assertThatThrownBy(() -> couponService.createCoupon(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_CODE_DUPLICATE.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 쿠폰 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("쿠폰 삭제")
    class DeleteCoupon {

        @Test
        @DisplayName("성공")
        void deleteCoupon_success() {
            Coupon coupon = mock(Coupon.class);
            given(coupon.getIssuedCount()).willReturn(0);
            given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));

            couponService.deleteCoupon(1L);

            then(couponRepository).should().delete(coupon);
        }

        @Test
        @DisplayName("실패 - 이미 발급된 쿠폰")
        void deleteCoupon_fail_hasIssued() {
            Coupon coupon = mock(Coupon.class);
            given(coupon.getIssuedCount()).willReturn(5);
            given(couponRepository.findById(1L)).willReturn(Optional.of(coupon));

            assertThatThrownBy(() -> couponService.deleteCoupon(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_HAS_ISSUED.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 쿠폰")
        void deleteCoupon_fail_notFound() {
            given(couponRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> couponService.deleteCoupon(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 쿠폰 발급
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {

        @Test
        @DisplayName("성공")
        void issueCoupon_success() throws InterruptedException {
            User user = buildUser();
            Coupon coupon = buildCoupon(true);
            RLock lock = buildLock(true);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(couponRepository.findByCode("SAVE10")).willReturn(Optional.of(coupon));
            given(userCouponRepository.existsByUserIdAndCouponId(1L, 1L)).willReturn(false);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));

            couponService.issueCoupon(1L, "SAVE10");

            then(userCouponRepository).should().save(any(UserCoupon.class));
        }

        @Test
        @DisplayName("실패 - 락 획득 실패")
        void issueCoupon_fail_lockFail() throws InterruptedException {
            RLock lock = buildLock(false);
            given(redissonClient.getLock(anyString())).willReturn(lock);

            assertThatThrownBy(() -> couponService.issueCoupon(1L, "SAVE10"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_CONCURRENT_FAIL.getMessage());
        }

        @Test
        @DisplayName("실패 - 쿠폰 발급 불가 상태")
        void issueCoupon_fail_notAvailable() throws InterruptedException {
            Coupon coupon = buildCoupon(false);
            RLock lock = buildLock(true);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(couponRepository.findByCode("SAVE10")).willReturn(Optional.of(coupon));

            assertThatThrownBy(() -> couponService.issueCoupon(1L, "SAVE10"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_NOT_AVAILABLE.getMessage());
        }

        @Test
        @DisplayName("실패 - 이미 발급된 쿠폰")
        void issueCoupon_fail_alreadyIssued() throws InterruptedException {
            Coupon coupon = buildCoupon(true);
            RLock lock = buildLock(true);

            given(redissonClient.getLock(anyString())).willReturn(lock);
            given(couponRepository.findByCode("SAVE10")).willReturn(Optional.of(coupon));
            given(userCouponRepository.existsByUserIdAndCouponId(1L, 1L)).willReturn(true);

            assertThatThrownBy(() -> couponService.issueCoupon(1L, "SAVE10"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.COUPON_ALREADY_ISSUED.getMessage());
        }
    }
}
