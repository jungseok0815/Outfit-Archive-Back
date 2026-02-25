package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(4)
public class ProductInit implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<Brand> brands = brandRepository.findAll();
        Brand nike   = brands.get(0); // 나이키
        Brand adidas = brands.get(1); // 아디다스
        Brand zara   = brands.get(2); // 자라

        productRepository.save(Product.builder()
                .productNm("나이키 에어포스 1")
                .productCode("NK-001")
                .productPrice(119000)
                .productQuantity(50)
                .category(ProductCategory.SHOES)
                .brand(nike)
                .build());

        productRepository.save(Product.builder()
                .productNm("나이키 드라이핏 후디")
                .productCode("NK-002")
                .productPrice(99000)
                .productQuantity(30)
                .category(ProductCategory.TOP)
                .brand(nike)
                .build());

        productRepository.save(Product.builder()
                .productNm("아디다스 트레이닝 팬츠")
                .productCode("AD-001")
                .productPrice(89000)
                .productQuantity(40)
                .category(ProductCategory.BOTTOM)
                .brand(adidas)
                .build());

        productRepository.save(Product.builder()
                .productNm("아디다스 클로버 백팩")
                .productCode("AD-002")
                .productPrice(59000)
                .productQuantity(20)
                .category(ProductCategory.BAG)
                .brand(adidas)
                .build());

        productRepository.save(Product.builder()
                .productNm("자라 오버핏 자켓")
                .productCode("ZR-001")
                .productPrice(159000)
                .productQuantity(25)
                .category(ProductCategory.OUTER)
                .brand(zara)
                .build());

        log.info("[Init] Product 초기 데이터 생성 완료 - 5건");
    }
}
