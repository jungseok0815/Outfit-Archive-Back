package com.fasthub.backend.oper.product.repository;

import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
