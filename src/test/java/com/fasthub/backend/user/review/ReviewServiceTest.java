package com.fasthub.backend.user.review;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.OrderStatus;
import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.review.dto.InsertReviewDto;
import com.fasthub.backend.user.review.dto.ResponseReviewDto;
import com.fasthub.backend.user.review.dto.UpdateReviewDto;
import com.fasthub.backend.user.review.entity.Review;
import com.fasthub.backend.user.review.repository.ReviewRepository;
import com.fasthub.backend.user.review.service.ReviewService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 테스트")
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthRepository authRepository;

    // ────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────
    private User buildUser(Long id) {
        return User.builder()
                .id(id)
                .userId("user" + id)
                .userNm("사용자" + id)
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    private Product buildProduct() {
        return Product.builder()
                .id(10L)
                .productNm("테스트 상품")
                .productCode("P001")
                .productPrice(50000)
                .productQuantity(100)
                .category(null)
                .build();
    }

    private Order buildOrder(User user, Product product, OrderStatus status) {
        return Order.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(1)
                .totalPrice(50000)
                .usedPoint(0)
                .status(status)
                .shippingAddress("서울시")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .tossOrderId("toss-order-001")
                .build();
    }

    private Review buildReview(User user, Order order, Product product) {
        return Review.builder()
                .id(1L)
                .user(user)
                .order(order)
                .product(product)
                .rating(4)
                .content("좋은 상품입니다.")
                .build();
    }

    // ────────────────────────────────────────────────
    // 후기 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("후기 등록")
    class Insert {

        @Test
        @DisplayName("성공")
        void insert_success() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);
            given(dto.getRating()).willReturn(4);
            given(dto.getContent()).willReturn("좋은 상품입니다.");

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrder(order)).willReturn(false);
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            ResponseReviewDto result = reviewService.insert(1L, dto);

            assertThat(result.getRating()).isEqualTo(4);
            assertThat(result.getContent()).isEqualTo("좋은 상품입니다.");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void insert_fail_userNotFound() {
            InsertReviewDto dto = mock(InsertReviewDto.class);

            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.insert(999L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

            then(reviewRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주문")
        void insert_fail_orderNotFound() {
            User user = buildUser(1L);
            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(999L);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 본인 주문이 아님")
        void insert_fail_unauthorized() {
            User user = buildUser(1L);
            User otherUser = buildUser(2L);
            Product product = buildProduct();
            Order order = buildOrder(otherUser, product, OrderStatus.DELIVERED);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_UNAUTHORIZED.getMessage());
        }

        @Test
        @DisplayName("실패 - 배송 완료 상태가 아님")
        void insert_fail_orderNotDelivered() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.SHIPPING);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_ORDER_NOT_DELIVERED.getMessage());
        }

        @Test
        @DisplayName("실패 - 이미 후기를 작성한 주문")
        void insert_fail_alreadyExists() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrder(order)).willReturn(true);

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_ALREADY_EXISTS.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 평점 (0)")
        void insert_fail_invalidRating_zero() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);
            given(dto.getRating()).willReturn(0);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrder(order)).willReturn(false);

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_INVALID_RATING.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 평점 (6)")
        void insert_fail_invalidRating_six() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);

            InsertReviewDto dto = mock(InsertReviewDto.class);
            given(dto.getOrderId()).willReturn(100L);
            given(dto.getRating()).willReturn(6);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(orderRepository.findById(100L)).willReturn(Optional.of(order));
            given(reviewRepository.existsByOrder(order)).willReturn(false);

            assertThatThrownBy(() -> reviewService.insert(1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_INVALID_RATING.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 후기 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("후기 수정")
    class Update {

        @Test
        @DisplayName("성공")
        void update_success() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            UpdateReviewDto dto = mock(UpdateReviewDto.class);
            given(dto.getRating()).willReturn(5);
            given(dto.getContent()).willReturn("정말 좋아요!");

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            ResponseReviewDto result = reviewService.update(1L, 1L, dto);

            assertThat(result.getRating()).isEqualTo(5);
            assertThat(result.getContent()).isEqualTo("정말 좋아요!");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 후기")
        void update_fail_reviewNotFound() {
            UpdateReviewDto dto = mock(UpdateReviewDto.class);

            given(reviewRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.update(1L, 999L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("실패 - 본인 후기가 아님")
        void update_fail_unauthorized() {
            User user = buildUser(1L);
            User otherUser = buildUser(2L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            UpdateReviewDto dto = mock(UpdateReviewDto.class);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.update(2L, 1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_UNAUTHORIZED.getMessage());
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 평점")
        void update_fail_invalidRating() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            UpdateReviewDto dto = mock(UpdateReviewDto.class);
            given(dto.getRating()).willReturn(6);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.update(1L, 1L, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_INVALID_RATING.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 후기 삭제 (사용자)
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("후기 삭제 (사용자)")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            reviewService.delete(1L, 1L);

            then(reviewRepository).should().delete(review);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 후기")
        void delete_fail_reviewNotFound() {
            given(reviewRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.delete(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());

            then(reviewRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("실패 - 본인 후기가 아님")
        void delete_fail_unauthorized() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.delete(2L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_UNAUTHORIZED.getMessage());

            then(reviewRepository).should(never()).delete(any());
        }
    }

    // ────────────────────────────────────────────────
    // 후기 삭제 (관리자)
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("후기 삭제 (관리자)")
    class DeleteByAdmin {

        @Test
        @DisplayName("성공")
        void deleteByAdmin_success() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            reviewService.deleteByAdmin(1L);

            then(reviewRepository).should().delete(review);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 후기")
        void deleteByAdmin_fail_reviewNotFound() {
            given(reviewRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.deleteByAdmin(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());

            then(reviewRepository).should(never()).delete(any());
        }
    }

    // ────────────────────────────────────────────────
    // 상품별 후기 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("상품별 후기 조회")
    class GetByProduct {

        @Test
        @DisplayName("성공")
        void getByProduct_success() {
            Pageable pageable = PageRequest.of(0, 10);
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            given(productRepository.findById(10L)).willReturn(Optional.of(product));
            given(reviewRepository.findByProduct(product, pageable))
                    .willReturn(new PageImpl<>(List.of(review)));

            Page<ResponseReviewDto> result = reviewService.getByProduct(10L, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getRating()).isEqualTo(4);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void getByProduct_fail_productNotFound() {
            Pageable pageable = PageRequest.of(0, 10);

            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.getByProduct(999L, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_SELECT.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 후기 단건 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("후기 단건 조회")
    class GetById {

        @Test
        @DisplayName("성공")
        void getById_success() {
            User user = buildUser(1L);
            Product product = buildProduct();
            Order order = buildOrder(user, product, OrderStatus.DELIVERED);
            Review review = buildReview(user, order, product);

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            ResponseReviewDto result = reviewService.getById(1L);

            assertThat(result.getRating()).isEqualTo(4);
            assertThat(result.getContent()).isEqualTo("좋은 상품입니다.");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 후기")
        void getById_fail_notFound() {
            given(reviewRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.getById(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
        }
    }
}
