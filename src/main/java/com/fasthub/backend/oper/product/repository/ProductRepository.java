package com.fasthub.backend.oper.product.repository;

import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;


public interface ProductRepository extends JpaRepository<Product, Long> {

}
