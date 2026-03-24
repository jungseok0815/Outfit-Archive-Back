package com.fasthub.backend.user.coupon.controller;

import com.fasthub.backend.user.coupon.dto.UserCouponDto;
import com.fasthub.backend.user.coupon.service.CouponService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usr/coupon")
@RequiredArgsConstructor
public class UserCouponController {

    private final CouponService couponService;

    // 쿠폰 코드 입력으로 발급
    // POST /api/usr/coupon/issue?code=SUMMER2024
    @PostMapping("/issue")
    public ResponseEntity<Void> issueCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code) {
        couponService.issueCoupon(userDetails.getId(), code);
        return ResponseEntity.ok().build();
    }

    // 보유 쿠폰 목록 조회 (미사용만)
    // GET /api/usr/coupon
    @GetMapping
    public ResponseEntity<List<UserCouponDto>> getMyCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(couponService.getMyCoupons(userDetails.getId()));
    }
}
