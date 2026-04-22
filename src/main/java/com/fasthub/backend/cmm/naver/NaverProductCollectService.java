package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.category.repository.CategoryRepository;
import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import com.fasthub.backend.admin.keyword.repository.CollectKeywordRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.event.BulkProductSavedEvent;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import com.fasthub.backend.user.recommend.client.ClipClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverProductCollectService {

    private final NaverShoppingClient naverShoppingClient;
    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ProductSizeRepository productSizeRepository;
    private final BrandRepository brandRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ClipClient clipClient;
    private final CollectKeywordRepository collectKeywordRepository;
    private final CategoryRepository categoryRepository;

    private static final int DEFAULT_QUANTITY = 50;

    // 트랜잭션 per item 적용을 위한 self-injection
    @Autowired
    @Lazy
    private NaverProductCollectService self;

    private static final int DISPLAY_PER_KEYWORD = 50;

    public void collect() {
        List<CollectKeyword> keywords = collectKeywordRepository.findAllByActiveTrue();
        log.info("[Naver수집] 시작 - 총 {}개 키워드, 목표 {}건", keywords.size(), keywords.size() * DISPLAY_PER_KEYWORD);
        List<Long> savedIds = new ArrayList<>();
        int skipCount = 0;

        for (CollectKeyword kc : keywords) {
            List<NaverShoppingItem> items = naverShoppingClient.search(kc.getKeyword(), DISPLAY_PER_KEYWORD);
            for (NaverShoppingItem item : items) {
                if (item.getProductId() == null || item.getImage() == null) continue;
                try {
                    Long savedId = self.saveProduct(item, kc.getCategory());
                    if (savedId != null) {
                        savedIds.add(savedId);
                    } else {
                        skipCount++;
                    }
                } catch (Exception e) {
                    log.warn("[Naver수집] 저장 실패 - productId={}, error={}", item.getProductId(), e.getMessage());
                }
            }
        }

        if (!savedIds.isEmpty()) {
            eventPublisher.publishEvent(new BulkProductSavedEvent(savedIds));
            log.info("[Naver수집] CLIP 벡터 추출 예약 - {}건", savedIds.size());
        }

        log.info("[Naver수집] 완료 - 신규={}건, 중복 스킵={}건", savedIds.size(), skipCount);
    }

    @Transactional
    public Long saveProduct(NaverShoppingItem item, Category category) {
        // 중복 체크
        if (productRepository.existsByNaverProductId(item.getProductId())) return null;

        // 단독 상품 이미지 여부 판별
        if (!clipClient.detectCleanProduct(item.getImage())) {
            log.debug("[Naver수집] 단독 상품 이미지 아님 - 스킵 productId={}", item.getProductId());
            return null;
        }

        Brand brand = findOrCreateBrand(item.getBrand());

        List<String> sizes = (category.getDefaultSizes() != null && !category.getDefaultSizes().isBlank())
                ? Arrays.asList(category.getDefaultSizes().split(","))
                : List.of("FREE");

        Product product = productRepository.save(Product.builder()
                .productNm(item.getCleanTitle())
                .productCode(item.getProductId())
                .productPrice(item.getPriceAsInt())
                .productQuantity(DEFAULT_QUANTITY * sizes.size())
                .category(category)
                .brand(brand)
                .naverProductId(item.getProductId())
                .build());

        // 이미지 URL 직접 저장 (S3 미사용)
        // imgNm = "" → ImgHandler.deleteFile() 에서 isBlank() 체크로 S3 삭제 스킵됨
        ProductImg img = new ProductImg();
        img.setImgPath(item.getImage());
        img.setImgNm("");
        img.setImgOriginNm(item.getCleanTitle());
        img.setMappingEntity(product);
        productImgRepository.save(img);

        sizes.forEach(sizeNm ->
                productSizeRepository.save(ProductSize.builder()
                        .product(product)
                        .sizeNm(sizeNm.trim())
                        .quantity(DEFAULT_QUANTITY)
                        .build())
        );

        log.debug("[Naver수집] 저장 완료 - productId={}, name={}", product.getId(), product.getProductNm());
        return product.getId();
    }

    @Async("collectExecutor")
    public void collectByBrands(SseEmitter emitter, List<Long> brandIds, List<Long> keywordIds) {
        List<Brand> brands = brandRepository.findAllById(brandIds);

        boolean useKeywords = keywordIds != null && !keywordIds.isEmpty();
        List<CollectKeyword> keywords = useKeywords
                ? collectKeywordRepository.findAllById(keywordIds)
                : List.of();

        log.info("[Naver수집-브랜드] 시작 - 브랜드 {}개, 키워드 {}개", brands.size(), keywords.size());
        List<Long> savedIds = new ArrayList<>();
        int skipCount = 0;

        try {
            if (keywords.isEmpty()) {
                List<Category> categories = categoryRepository.findAllByActiveTrue();
                int total = brands.size() * categories.size();
                int current = 0;
                for (Brand brand : brands) {
                    for (Category category : categories) {
                        current++;
                        String searchKeyword = brand.getBrandNm() + " " + category.getKorName();
                        sendProgress(emitter, brand.getBrandNm() + " " + category.getKorName() + " 검색 중...",
                                savedIds.size(), skipCount, current, total);
                        List<NaverShoppingItem> items = naverShoppingClient.search(searchKeyword, DISPLAY_PER_KEYWORD);
                        for (NaverShoppingItem item : items) {
                            if (item.getProductId() == null || item.getImage() == null) continue;
                            try {
                                Long savedId = self.saveProductForBrand(item, category, brand);
                                if (savedId != null) savedIds.add(savedId);
                                else skipCount++;
                            } catch (Exception e) {
                                log.warn("[Naver수집-브랜드] 저장 실패 - productId={}, error={}", item.getProductId(), e.getMessage());
                            }
                        }
                    }
                }
            }
            else {
                int total = brands.size() * keywords.size();
                int current = 0;
                for (Brand brand : brands) {
                    for (CollectKeyword kc : keywords) {
                        current++;
                        String searchKeyword = brand.getBrandNm() + " " + kc.getKeyword();
                        sendProgress(emitter, brand.getBrandNm() + " " + kc.getKeyword() + " 검색 중...",
                                savedIds.size(), skipCount, current, total);
                        List<NaverShoppingItem> items = naverShoppingClient.search(searchKeyword, DISPLAY_PER_KEYWORD);
                        for (NaverShoppingItem item : items) {
                            if (item.getProductId() == null || item.getImage() == null) continue;
                            try {
                                Long savedId = self.saveProductForBrand(item, kc.getCategory(), brand);
                                if (savedId != null) savedIds.add(savedId);
                                else skipCount++;
                            } catch (Exception e) {
                                log.warn("[Naver수집-브랜드] 저장 실패 - productId={}, error={}", item.getProductId(), e.getMessage());
                            }
                        }
                    }
                }
            }

            if (!savedIds.isEmpty()) {
                eventPublisher.publishEvent(new BulkProductSavedEvent(savedIds));
                log.info("[Naver수집-브랜드] CLIP 벡터 추출 예약 - {}건", savedIds.size());
            }

            log.info("[Naver수집-브랜드] 완료 - 신규={}건, 중복 스킵={}건", savedIds.size(), skipCount);
            emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(Map.of("saved", savedIds.size(), "skipped", skipCount)));
            emitter.complete();

        } catch (Exception e) {
            log.error("[Naver수집-브랜드] 오류 발생", e);
            try {
                emitter.send(SseEmitter.event().name("error").data(Map.of("message", e.getMessage())));
                emitter.complete();
            } catch (Exception ignored) {}
        }
    }

    private void sendProgress(SseEmitter emitter, String message, int saved, int skipped, int current, int total) {
        try {
            emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(Map.of("message", message, "saved", saved, "skipped", skipped,
                            "current", current, "total", total)));
        } catch (Exception ignored) {}
    }

    @Transactional
    public Long saveProductForBrand(NaverShoppingItem item, Category category, Brand brand) {
        if (productRepository.existsByNaverProductId(item.getProductId())) return null;

        if (!clipClient.detectCleanProduct(item.getImage())) {
            log.debug("[Naver수집-브랜드] 단독 상품 이미지 아님 - 스킵 productId={}", item.getProductId());
            return null;
        }

        List<String> sizes = (category.getDefaultSizes() != null && !category.getDefaultSizes().isBlank())
                ? Arrays.asList(category.getDefaultSizes().split(","))
                : List.of("FREE");

        Product product = productRepository.save(Product.builder()
                .productNm(item.getCleanTitle())
                .productCode(item.getProductId())
                .productPrice(item.getPriceAsInt())
                .productQuantity(DEFAULT_QUANTITY * sizes.size())
                .category(category)
                .brand(brand)
                .naverProductId(item.getProductId())
                .build());

        ProductImg img = new ProductImg();
        img.setImgPath(item.getImage());
        img.setImgNm("");
        img.setImgOriginNm(item.getCleanTitle());
        img.setMappingEntity(product);
        productImgRepository.save(img);

        sizes.forEach(sizeNm ->
                productSizeRepository.save(ProductSize.builder()
                        .product(product)
                        .sizeNm(sizeNm.trim())
                        .quantity(DEFAULT_QUANTITY)
                        .build())
        );

        log.debug("[Naver수집-브랜드] 저장 완료 - productId={}, brandNm={}", product.getId(), brand.getBrandNm());
        return product.getId();
    }

    private Brand findOrCreateBrand(String brandNm) {
        String name = (brandNm == null || brandNm.isBlank()) ? "기타" : brandNm.trim();
        return brandRepository.findByBrandNm(name)
                .orElseGet(() -> brandRepository.save(Brand.builder().brandNm(name).build()));
    }
}
