package com.fasthub.backend.admin.coupon.controller;

import com.fasthub.backend.user.coupon.dto.CouponCreateDto;
import com.fasthub.backend.user.coupon.dto.CouponDto;
import com.fasthub.backend.user.coupon.dto.CouponUpdateDto;
import com.fasthub.backend.user.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupon")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    // 쿠폰 목록 조회
    // GET /api/admin/coupon/list
    @GetMapping("/list")
    public ResponseEntity<List<CouponDto>> getCouponList() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // 쿠폰 생성
    // POST /api/admin/coupon
    @PostMapping
    public ResponseEntity<Void> createCoupon(@RequestBody CouponCreateDto dto) {
        couponService.createCoupon(dto);
        return ResponseEntity.ok().build();
    }

    // 쿠폰 수정
    // PUT /api/admin/coupon/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCoupon(@PathVariable Long id, @RequestBody CouponUpdateDto dto) {
        couponService.updateCoupon(id, dto);
        return ResponseEntity.ok().build();
    }

    // 쿠폰 삭제
    // DELETE /api/admin/coupon/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok().build();
    }
}
