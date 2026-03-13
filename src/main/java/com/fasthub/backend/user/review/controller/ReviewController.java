package com.fasthub.backend.user.review.controller;

import com.fasthub.backend.user.review.dto.InsertReviewDto;
import com.fasthub.backend.user.review.dto.ResponseReviewDto;
import com.fasthub.backend.user.review.dto.UpdateReviewDto;
import com.fasthub.backend.user.review.service.ReviewService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 후기 작성
    @PostMapping
    public ResponseEntity<ResponseReviewDto> insert(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody InsertReviewDto dto) {
        return ResponseEntity.status(201).body(reviewService.insert(userDetails.getId(), dto));
    }

    // 후기 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ResponseReviewDto> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewDto dto) {
        return ResponseEntity.ok(reviewService.update(userDetails.getId(), reviewId, dto));
    }

    // 후기 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {
        reviewService.delete(userDetails.getId(), reviewId);
        return ResponseEntity.noContent().build();
    }

    // 상품별 후기 목록 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ResponseReviewDto>> getByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getByProduct(productId, pageable));
    }

    // 후기 단건 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseReviewDto> getById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getById(reviewId));
    }
}
