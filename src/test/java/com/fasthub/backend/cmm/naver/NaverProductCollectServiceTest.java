package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.event.BulkProductSavedEvent;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.repository.ProductSizeRepository;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import com.fasthub.backend.user.recommend.client.ClipClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NaverProductCollectService 테스트")
class NaverProductCollectServiceTest {

    @InjectMocks
    private NaverProductCollectService naverProductCollectService;

    @Mock private NaverShoppingClient naverShoppingClient;
    @Mock private ProductRepository productRepository;
    @Mock private ProductImgRepository productImgRepository;
    @Mock private ProductSizeRepository productSizeRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ClipClient clipClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(naverProductCollectService, "self", naverProductCollectService);
    }

    private NaverShoppingItem buildItem(String productId, String title, String brand) {
        NaverShoppingItem item = mock(NaverShoppingItem.class);
        given(item.getProductId()).willReturn(productId);
        given(item.getImage()).willReturn("https://image.com/test.jpg");
        given(item.getCleanTitle()).willReturn(title);
        given(item.getBrand()).willReturn(brand);
        given(item.getPriceAsInt()).willReturn(50000);
        return item;
    }

    // ────────────────────────────────────────────────
    // saveProduct
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("saveProduct")
    class SaveProduct {

        @Test
        @DisplayName("성공 - 신규 상품 저장")
        void saveProduct_success() {
            NaverShoppingItem item = buildItem("NAVER001", "나이키 반팔티", "나이키");
            Brand brand = Brand.builder().brandNm("나이키").build();
            Product product = Product.builder().id(1L).productNm("나이키 반팔티").build();

            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(false);
            given(clipClient.detectCleanProduct("https://image.com/test.jpg")).willReturn(true);
            given(brandRepository.findByBrandNm("나이키")).willReturn(Optional.of(brand));
            given(productRepository.save(any(Product.class))).willReturn(product);

            Long savedId = naverProductCollectService.saveProduct(item, ProductCategory.TOP);

            assertThat(savedId).isEqualTo(1L);
            then(productRepository).should().save(any(Product.class));
            then(productImgRepository).should().save(any());
            then(productSizeRepository).should(atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("중복 - naverProductId 이미 존재하면 null 반환")
        void saveProduct_skip_duplicate() {
            NaverShoppingItem item = buildItem("NAVER001", "나이키 반팔티", "나이키");

            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(true);

            Long savedId = naverProductCollectService.saveProduct(item, ProductCategory.TOP);

            assertThat(savedId).isNull();
            then(productRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("스킵 - 단독 상품 이미지 아닌 경우 null 반환")
        void saveProduct_skip_notCleanProduct() {
            NaverShoppingItem item = buildItem("NAVER001", "나이키 반팔티", "나이키");

            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(false);
            given(clipClient.detectCleanProduct("https://image.com/test.jpg")).willReturn(false);

            Long savedId = naverProductCollectService.saveProduct(item, ProductCategory.TOP);

            assertThat(savedId).isNull();
            then(productRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 브랜드 없으면 기타로 생성")
        void saveProduct_createBrand_whenNotFound() {
            NaverShoppingItem item = buildItem("NAVER002", "무브랜드 티셔츠", null);
            Brand brand = Brand.builder().brandNm("기타").build();
            Product product = Product.builder().id(2L).productNm("무브랜드 티셔츠").build();

            given(productRepository.existsByNaverProductId("NAVER002")).willReturn(false);
            given(clipClient.detectCleanProduct("https://image.com/test.jpg")).willReturn(true);
            given(brandRepository.findByBrandNm("기타")).willReturn(Optional.empty());
            given(brandRepository.save(any(Brand.class))).willReturn(brand);
            given(productRepository.save(any(Product.class))).willReturn(product);

            Long savedId = naverProductCollectService.saveProduct(item, ProductCategory.TOP);

            assertThat(savedId).isEqualTo(2L);
            then(brandRepository).should().save(any(Brand.class));
        }
    }

    // ────────────────────────────────────────────────
    // collect
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("collect")
    class Collect {

        @Test
        @DisplayName("성공 - 신규 상품 저장 후 이벤트 발행")
        void collect_success_publishEvent() {
            NaverShoppingItem item = buildItem("NAVER001", "청바지", "리바이스");
            Brand brand = Brand.builder().brandNm("리바이스").build();
            Product product = Product.builder().id(1L).productNm("청바지").build();

            given(naverShoppingClient.search(anyString(), anyInt())).willReturn(List.of(item));
            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(false);
            given(clipClient.detectCleanProduct("https://image.com/test.jpg")).willReturn(true);
            given(brandRepository.findByBrandNm("리바이스")).willReturn(Optional.of(brand));
            given(productRepository.save(any(Product.class))).willReturn(product);

            naverProductCollectService.collect();

            ArgumentCaptor<BulkProductSavedEvent> captor = ArgumentCaptor.forClass(BulkProductSavedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());
            assertThat(captor.getValue().productIds()).isNotEmpty();
        }

        @Test
        @DisplayName("전부 중복이면 이벤트 미발행")
        void collect_allDuplicate_noEvent() {
            NaverShoppingItem item = buildItem("NAVER001", "청바지", "리바이스");

            given(naverShoppingClient.search(anyString(), anyInt())).willReturn(List.of(item));
            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(true);

            naverProductCollectService.collect();

            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("단독 상품 아닌 이미지만 있으면 이벤트 미발행")
        void collect_allNotClean_noEvent() {
            NaverShoppingItem item = buildItem("NAVER001", "청바지", "리바이스");

            given(naverShoppingClient.search(anyString(), anyInt())).willReturn(List.of(item));
            given(productRepository.existsByNaverProductId("NAVER001")).willReturn(false);
            given(clipClient.detectCleanProduct("https://image.com/test.jpg")).willReturn(false);

            naverProductCollectService.collect();

            then(eventPublisher).should(never()).publishEvent(any());
        }

        @Test
        @DisplayName("productId 없는 아이템은 스킵")
        void collect_skipItem_whenProductIdNull() {
            NaverShoppingItem item = mock(NaverShoppingItem.class);
            given(item.getProductId()).willReturn(null);

            given(naverShoppingClient.search(anyString(), anyInt())).willReturn(List.of(item));

            naverProductCollectService.collect();

            then(productRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("image 없는 아이템은 스킵")
        void collect_skipItem_whenImageNull() {
            NaverShoppingItem item = mock(NaverShoppingItem.class);
            given(item.getProductId()).willReturn("NAVER001");
            given(item.getImage()).willReturn(null);

            given(naverShoppingClient.search(anyString(), anyInt())).willReturn(List.of(item));

            naverProductCollectService.collect();

            then(productRepository).should(never()).save(any());
        }
    }
}
