package com.fasthub.backend.admin.order.repository;

import com.fasthub.backend.admin.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE :keyword IS NULL OR :keyword = '' OR o.user.userNm LIKE %:keyword%")
    Page<Order> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
