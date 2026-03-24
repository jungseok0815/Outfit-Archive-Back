package com.fasthub.backend.user.coupon.repository;

import com.fasthub.backend.user.coupon.entity.UserCoupon;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    // 중복 발급 체크
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    // 미사용 쿠폰 목록 (발급일 내림차순)
    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon WHERE uc.user = :user AND uc.isUsed = false ORDER BY uc.issuedAt DESC")
    List<UserCoupon> findAvailableByUser(@Param("user") User user);
}
