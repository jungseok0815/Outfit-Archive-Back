package com.fasthub.backend.user.review.repository;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.user.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrder(Order order);

    Page<Review> findByProduct(Product product, Pageable pageable);
}
