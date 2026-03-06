package com.fasthub.backend.admin.product.repository;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImgRepository extends JpaRepository<ProductImg, Long> {
    void deleteByProduct(Product product);
    List<ProductImg> findByProduct(Product product);
}
