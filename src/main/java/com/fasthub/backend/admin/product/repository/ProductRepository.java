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

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.brand.id = :brandId")
    List<Product> findAllByBrandIdWithImages(@Param("brandId") Long brandId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id IN :ids")
    List<Product> findAllByIdInWithImages(@Param("ids") List<Long> ids);

    @Override
    @EntityGraph(attributePaths = {"images"})
    List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE :keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%")
    Page<Product> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR :keyword = '' OR p.productNm LIKE %:keyword%) AND (:category IS NULL OR p.category = :category)")
    Page<Product> findAllByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") ProductCategory category, Pageable pageable);

    boolean existsByNaverProductId(String naverProductId);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.embedding IS NOT NULL AND p.hidden = false")
    List<Product> findAllWithEmbedding();

    @Query("SELECT p FROM Product p WHERE " +
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

}
