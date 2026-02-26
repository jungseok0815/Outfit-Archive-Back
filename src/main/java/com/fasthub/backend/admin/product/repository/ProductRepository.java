package com.fasthub.backend.admin.product.repository;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE :keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%")
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND (:category IS NULL OR p.category = :category)")
    Page<Product> findAllByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") ProductCategory category, Pageable pageable);

}
