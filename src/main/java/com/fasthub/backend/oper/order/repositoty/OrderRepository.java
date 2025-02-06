package com.fasthub.backend.oper.order.repositoty;

import com.fasthub.backend.oper.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
