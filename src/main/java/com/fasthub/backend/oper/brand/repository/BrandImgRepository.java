package com.fasthub.backend.oper.brand.repository;

import com.fasthub.backend.oper.brand.entity.Brand;
import com.fasthub.backend.oper.brand.entity.BrandImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandImgRepository extends JpaRepository<BrandImg, Long> {
    void deleteByBrand(Brand brand);
}
