package com.fasthub.backend.user.product.repository;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "p.hidden = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
            "(:minPrice IS NULL OR p.productPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.productPrice <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("category") ProductCategory category,
            @Param("brandId") Long brandId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE " +
            "p.hidden = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
            "(:minPrice IS NULL OR p.productPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.productPrice <= :maxPrice) " +
            "ORDER BY (SELECT COUNT(o) FROM Order o WHERE o.product.id = p.id) DESC",
           countQuery = "SELECT COUNT(p) FROM Product p WHERE " +
            "p.hidden = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
            "(:minPrice IS NULL OR p.productPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.productPrice <= :maxPrice)")
    Page<Product> searchProductsByPopularity(
            @Param("keyword") String keyword,
            @Param("category") ProductCategory category,
            @Param("brandId") Long brandId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);
}
