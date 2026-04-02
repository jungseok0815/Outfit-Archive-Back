package com.fasthub.backend.user.review.service;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.user.review.dto.InsertReviewDto;
import com.fasthub.backend.user.review.dto.ResponseReviewDto;
import com.fasthub.backend.user.review.dto.UpdateReviewDto;
import com.fasthub.backend.user.review.entity.Review;
import com.fasthub.backend.user.review.entity.ReviewImg;
import com.fasthub.backend.user.review.repository.ReviewImgRepository;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthRepository authRepository;
    private final ImgHandler imgHandler;

    @Transactional
    public ResponseReviewDto insert(Long userId, InsertReviewDto dto) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_UNAUTHORIZED);
        }

        // 배송 완료된 주문만 후기 작성 가능
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorCode.REVIEW_ORDER_NOT_DELIVERED);
        }

        // 이미 후기를 작성한 주문인지 확인
        if (reviewRepository.existsByOrder(order)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 평점 유효성 검사
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BusinessException(ErrorCode.REVIEW_INVALID_RATING);
        }

        Review review = reviewRepository.save(Review.builder()
                .user(user)
                .order(order)
                .product(order.getProduct())
                .rating(dto.getRating())
                .content(dto.getContent())
                .build());

        if (dto.getImages() != null) {
            dto.getImages().forEach(image ->
                    reviewImgRepository.save(imgHandler.createImg(image, ReviewImg::new, review)));
        }

        log.info("[Review] 후기 작성 완료 userId={}, orderId={}, rating={}", userId, dto.getOrderId(), dto.getRating());

        return ResponseReviewDto.of(review);
    }

    @Transactional
    public ResponseReviewDto update(Long userId, Long reviewId, UpdateReviewDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_UNAUTHORIZED);
        }

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BusinessException(ErrorCode.REVIEW_INVALID_RATING);
        }

        review.update(dto.getRating(), dto.getContent());

        log.info("[Review] 후기 수정 완료 userId={}, reviewId={}", userId, reviewId);

        return ResponseReviewDto.of(review);
    }

    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_UNAUTHORIZED);
        }

        reviewRepository.delete(review);

        log.info("[Review] 후기 삭제 완료 userId={}, reviewId={}", userId, reviewId);
    }

    @Transactional
    public void deleteByAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
        log.info("[Review] 관리자 후기 삭제 완료 reviewId={}", reviewId);
    }

    @Transactional(readOnly = true)
    public Page<ResponseReviewDto> getByProduct(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_SELECT));

        return reviewRepository.findByProduct(product, pageable)
                .map(ResponseReviewDto::of);
    }

    @Transactional(readOnly = true)
    public ResponseReviewDto getById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
        return ResponseReviewDto.of(review);
    }
}
