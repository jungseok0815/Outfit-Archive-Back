package com.fasthub.backend.oper.product.repository;

import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImgRepository extends JpaRepository<ProductImg, Long> {
    void deleteByProduct(Product product);
    void findProductImgByProduct(Product product);
}
