package com.fasthub.backend.user.review.repository;

import com.fasthub.backend.user.review.entity.Review;
import com.fasthub.backend.user.review.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    void deleteByReview(Review review);
}
