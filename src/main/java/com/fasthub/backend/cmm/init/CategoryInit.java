package com.fasthub.backend.cmm.init;

import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.category.repository.CategoryRepository;
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
@Order(2)
public class CategoryInit implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0) return;

        categoryRepository.saveAll(List.of(
                Category.builder().name("TOP").korName("상의").engName("Tops").defaultSizes("S,M,L,XL").active(true).build(),
                Category.builder().name("BOTTOM").korName("하의").engName("Bottoms").defaultSizes("S,M,L,XL").active(true).build(),
                Category.builder().name("OUTER").korName("아우터").engName("Outer").defaultSizes("S,M,L,XL").active(true).build(),
                Category.builder().name("DRESS").korName("원피스/세트").engName("Dress/Sets").defaultSizes("S,M,L,XL").active(true).build(),
                Category.builder().name("SHOES").korName("신발").engName("Shoes").defaultSizes("230,240,250,260,270").active(true).build(),
                Category.builder().name("BAG").korName("가방").engName("Bags").defaultSizes("FREE").active(true).build()
        ));

        log.info("[Init] Category 초기 데이터 생성 완료 - 6건");
    }
}
