package com.fasthub.backend.admin.review.controller;

import com.fasthub.backend.admin.auth.dto.AdminCustomUserDetails;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.AdminRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.review.dto.ResponseReviewDto;
import com.fasthub.backend.user.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/review")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;
    private final ProductRepository productRepository;

    // 상품별 리뷰 목록 조회
    // PARTNER: 자신의 브랜드 상품만 조회 가능
    // SUPER_ADMIN / ADMIN: 전체 조회 가능
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ResponseReviewDto>> getByProduct(
            @AuthenticationPrincipal AdminCustomUserDetails adminDetails,
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        if (adminDetails.getAdminMemberDto().getAdminRole() == AdminRole.PARTNER) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));

            Long adminBrandId = adminDetails.getAdminMemberDto().getBrandId();
            if (adminBrandId == null || !adminBrandId.equals(product.getBrand().getId())) {
                throw new BusinessException(ErrorCode.ADMIN_ACCESS_DENIED);
            }
        }

        return ResponseEntity.ok(reviewService.getByProduct(productId, pageable));
    }

    // 리뷰 삭제 - SUPER_ADMIN만 가능
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal AdminCustomUserDetails adminDetails,
            @PathVariable Long reviewId) {

        if (adminDetails.getAdminMemberDto().getAdminRole() != AdminRole.SUPER_ADMIN) {
            throw new BusinessException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        reviewService.deleteByAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }
}
