package com.fasthub.backend.oper.product.repository;

import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE :keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%")
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
