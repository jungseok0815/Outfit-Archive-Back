package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import com.fasthub.backend.admin.keyword.repository.CollectKeywordRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import com.fasthub.backend.admin.product.event.BulkProductSavedEvent;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import com.fasthub.backend.user.recommend.client.ClipClient;
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
    private final ClipClient clipClient;
    private final CollectKeywordRepository collectKeywordRepository;

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
    public Long saveProduct(NaverShoppingItem item, ProductCategory category) {
        // 중복 체크
        if (productRepository.existsByNaverProductId(item.getProductId())) {
            return null;
        }

        // 단독 상품 이미지 여부 판별
        if (!clipClient.detectCleanProduct(item.getImage())) {
            log.debug("[Naver수집] 단독 상품 이미지 아님 - 스킵 productId={}", item.getProductId());
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
