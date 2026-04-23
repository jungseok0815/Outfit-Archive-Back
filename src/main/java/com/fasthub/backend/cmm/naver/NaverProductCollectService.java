package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import com.fasthub.backend.admin.keyword.repository.CollectKeywordRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import com.fasthub.backend.user.recommend.client.ClipClient;
import com.fasthub.backend.user.similar.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ClipClient clipClient;
    private final CollectKeywordRepository collectKeywordRepository;
    private final EmbeddingService embeddingService;

    private static final int DEFAULT_QUANTITY = 50;

    // 트랜잭션 per item 적용을 위한 self-injection
    @Autowired
    @Lazy
    private NaverProductCollectService self;

    private static final int DISPLAY_PER_KEYWORD = 50;

    @Async("collectExecutor")
    public void collectByBrands(SseEmitter emitter, List<Long> brandIds, List<Long> keywordIds) {
        List<Brand> brands = brandRepository.findAllById(brandIds);

        boolean useKeywords = keywordIds != null && !keywordIds.isEmpty();
        List<CollectKeyword> keywords = useKeywords
                ? collectKeywordRepository.findAllByIdWithCategory(keywordIds)
                : List.of();

        if (keywords.isEmpty()) {
            log.warn("[Naver수집-브랜드] 키워드 없음 - 수집 중단");
            try {
                emitter.send(SseEmitter.event().name("error").data(Map.of("message", "키워드가 없습니다.")));
                emitter.complete();
            } catch (Exception ignored) {}
            return;
        }

        log.info("[Naver수집-브랜드] 시작 - 브랜드 {}개, 키워드 {}개", brands.size(), keywords.size());
        List<Long> savedIds = new ArrayList<>();
        int skipCount = 0;

        try {
            int total = brands.size() * keywords.size();
            int current = 0;
            for (Brand brand : brands) {
                for (CollectKeyword kc : keywords) {
                    current++;
                    String searchKeyword = brand.getBrandNm() + " " + kc.getKeyword();
                    sendProgress(emitter, brand.getBrandNm() + " " + kc.getKeyword() + " 검색 중...",
                            savedIds.size(), skipCount, current, total);
                    List<NaverShoppingItem> items = naverShoppingClient.search(searchKeyword, DISPLAY_PER_KEYWORD);
                    List<Long> batchSaved = self.saveProductForBrandBatch(items, kc.getCategory(), brand);
                    savedIds.addAll(batchSaved);
                }
            }

            log.info("[Naver수집-브랜드] 상품 수집 완료 - 신규={}건, 중복 스킵={}건", savedIds.size(), skipCount);

            if (!savedIds.isEmpty()) {
                log.info("[Naver수집-브랜드] 배치 임베딩 예약 - {}건", savedIds.size());
                embeddingService.generateAndSaveBatch(savedIds);
            }

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
    public List<Long> saveProductForBrandBatch(List<NaverShoppingItem> items, Category category, Brand brand) {
        if (items == null || items.isEmpty()) {
            log.debug("{}  검색 결과 없음!", brand.getBrandNm());
            return List.of();
        }

        List<NaverShoppingItem> newItems = items.stream()
                .filter(item -> !productRepository.existsByNaverProductId(item.getProductId()))
                .toList();

        if (newItems.isEmpty()) return List.of();

        List<String> imageUrls = newItems.stream().map(NaverShoppingItem::getImage).toList();
        Map<String, Boolean> cleanResults = clipClient.detectCleanProductBatch(imageUrls);
        log.info("[Naver수집-브랜드] 배치 클린 체크 결과 - 총={}건, 통과={}건, 스킵={}건",
                cleanResults.size(),
                cleanResults.values().stream().filter(v -> v).count(),
                cleanResults.values().stream().filter(v -> !v).count());
        cleanResults.forEach((url, clean) ->
                log.debug("[Naver수집-브랜드] 클린 체크 - url={}, clean={}", url, clean));

        List<String> sizes = (category.getDefaultSizes() != null && !category.getDefaultSizes().isBlank())
                ? Arrays.asList(category.getDefaultSizes().split(","))
                : List.of("FREE");

        List<Long> savedIds = new ArrayList<>();

        for (NaverShoppingItem item : newItems) {
            if (!cleanResults.getOrDefault(item.getImage(), false)) {
                log.debug("[Naver수집-브랜드] 단독 상품 이미지 아님 - 스킵 productId={}", item.getProductId());
                continue;
            }

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
            savedIds.add(product.getId());
        }

        return savedIds;
    }
}
