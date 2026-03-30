package com.fasthub.backend.user.coupon.repository;

import com.fasthub.backend.user.coupon.entity.UserCoupon;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    // 중복 발급 체크
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    // 전체 쿠폰 목록 - 미사용 먼저, 발급일 내림차순
    @Query("SELECT uc FROM UserCoupon uc JOIN FETCH uc.coupon WHERE uc.user = :user ORDER BY uc.isUsed ASC, uc.issuedAt DESC")
    List<UserCoupon> findAllByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM UserCoupon uc WHERE uc.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
