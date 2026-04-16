package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.event.BulkProductSavedEvent;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private static final int DEFAULT_QUANTITY = 50;

    private static final Map<ProductCategory, List<String>> DEFAULT_SIZES = Map.of(
            ProductCategory.TOP,    List.of("S", "M", "L", "XL"),
            ProductCategory.BOTTOM, List.of("S", "M", "L", "XL"),
            ProductCategory.OUTER,  List.of("S", "M", "L", "XL"),
            ProductCategory.DRESS,  List.of("S", "M", "L", "XL"),
            ProductCategory.SHOES,  List.of("230", "240", "250", "260", "270"),
            ProductCategory.BAG,    List.of("FREE")
    );

    // 트랜잭션 per item 적용을 위한 self-injection
    @Autowired
    @Lazy
    private NaverProductCollectService self;

    private static final int DISPLAY_PER_KEYWORD = 50;

    private record KeywordCategory(String keyword, ProductCategory category) {}

    private static final List<KeywordCategory> KEYWORDS = List.of(
            // TOP (5 × 50 = 250개)
            new KeywordCategory("반팔티", ProductCategory.TOP),
            new KeywordCategory("긴팔티", ProductCategory.TOP),
            new KeywordCategory("니트", ProductCategory.TOP),
            new KeywordCategory("맨투맨", ProductCategory.TOP),
            new KeywordCategory("후드티", ProductCategory.TOP),
            // BOTTOM (4 × 50 = 200개)
            new KeywordCategory("청바지", ProductCategory.BOTTOM),
            new KeywordCategory("슬랙스", ProductCategory.BOTTOM),
            new KeywordCategory("반바지", ProductCategory.BOTTOM),
            new KeywordCategory("스커트", ProductCategory.BOTTOM),
            // OUTER (4 × 50 = 200개)
            new KeywordCategory("패딩", ProductCategory.OUTER),
            new KeywordCategory("코트", ProductCategory.OUTER),
            new KeywordCategory("자켓", ProductCategory.OUTER),
            new KeywordCategory("트렌치코트", ProductCategory.OUTER),
            // DRESS (2 × 50 = 100개)
            new KeywordCategory("원피스", ProductCategory.DRESS),
            new KeywordCategory("투피스", ProductCategory.DRESS),
            // SHOES (4 × 50 = 200개)
            new KeywordCategory("운동화", ProductCategory.SHOES),
            new KeywordCategory("구두", ProductCategory.SHOES),
            new KeywordCategory("부츠", ProductCategory.SHOES),
            new KeywordCategory("샌들", ProductCategory.SHOES),
            // BAG (1 × 50 = 50개)
            new KeywordCategory("백팩", ProductCategory.BAG)
    );

    public void collect() {
        log.info("[Naver수집] 시작 - 총 {}개 키워드, 목표 {}건", KEYWORDS.size(), KEYWORDS.size() * DISPLAY_PER_KEYWORD);
        List<Long> savedIds = new ArrayList<>();
        int skipCount = 0;

        for (KeywordCategory kc : KEYWORDS) {
            List<NaverShoppingItem> items = naverShoppingClient.search(kc.keyword(), DISPLAY_PER_KEYWORD);

            for (NaverShoppingItem item : items) {
                if (item.getProductId() == null || item.getImage() == null) continue;

                try {
                    Long savedId = self.saveProduct(item, kc.category());
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
    public Long saveProduct(NaverShoppingItem item, ProductCategory category) {
        // 중복 체크
        if (productRepository.existsByNaverProductId(item.getProductId())) {
            return null;
        }

        Brand brand = findOrCreateBrand(item.getBrand());

        Product product = productRepository.save(Product.builder()
                .productNm(item.getCleanTitle())
                .productCode(item.getProductId())
                .productPrice(item.getPriceAsInt())
                .productQuantity(DEFAULT_QUANTITY * DEFAULT_SIZES.get(category).size())
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

        DEFAULT_SIZES.get(category).forEach(sizeNm ->
                productSizeRepository.save(ProductSize.builder()
                        .product(product)
                        .sizeNm(sizeNm)
                        .quantity(DEFAULT_QUANTITY)
                        .build())
        );

        log.debug("[Naver수집] 저장 완료 - productId={}, name={}", product.getId(), product.getProductNm());
        return product.getId();
    }

    private Brand findOrCreateBrand(String brandNm) {
        String name = (brandNm == null || brandNm.isBlank()) ? "기타" : brandNm.trim();
        return brandRepository.findByBrandNm(name)
                .orElseGet(() -> brandRepository.save(Brand.builder().brandNm(name).build()));
    }
}
