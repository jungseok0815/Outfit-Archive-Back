package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class BrandInit implements ApplicationRunner {

    private final BrandRepository brandRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (brandRepository.count() > 0) {
            return;
        }

        brandRepository.save(Brand.builder()
                .brandNm("나이키")
                .brandNum("02-1234-5678")
                .brandLocation("서울 강남구")
                .brandDc("세계적인 스포츠 브랜드")
                .build());

        brandRepository.save(Brand.builder()
                .brandNm("아디다스")
                .brandNum("02-2345-6789")
                .brandLocation("서울 마포구")
                .brandDc("독일 스포츠 패션 브랜드")
                .build());

        brandRepository.save(Brand.builder()
                .brandNm("자라")
                .brandNum("02-3456-7890")
                .brandLocation("서울 중구")
                .brandDc("스페인 패스트패션 브랜드")
                .build());

        log.info("[Init] Brand 초기 데이터 생성 완료 - 3건");
    }
}
