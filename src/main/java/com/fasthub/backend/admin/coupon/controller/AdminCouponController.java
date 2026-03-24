package com.fasthub.backend.admin.coupon.controller;

import com.fasthub.backend.user.coupon.dto.CouponCreateDto;
import com.fasthub.backend.user.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    // 쿠폰 생성
    // POST /api/admin/coupon
    @PostMapping
    public ResponseEntity<Void> createCoupon(@RequestBody CouponCreateDto dto) {
        couponService.createCoupon(dto);
        return ResponseEntity.ok().build();
    }
}
