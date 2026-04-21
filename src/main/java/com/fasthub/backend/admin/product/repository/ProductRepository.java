package com.fasthub.backend.admin.product.repository;

import com.fasthub.backend.admin.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.brand.id = :brandId")
    List<Product> findAllByBrandIdWithImages(@Param("brandId") Long brandId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id IN :ids")
    List<Product> findAllByIdInWithImages(@Param("ids") List<Long> ids);

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE :keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%")
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findAllByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    boolean existsByNaverProductId(String naverProductId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.embedding IS NOT NULL AND p.hidden = false")
    List<Product> findAllWithEmbedding();

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
            "(:minPrice IS NULL OR p.productPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.productPrice <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);
}
