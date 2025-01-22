package com.fasthub.backend.oper.brand.repository;

import com.fasthub.backend.oper.brand.entity.Brand;
import com.fasthub.backend.oper.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
