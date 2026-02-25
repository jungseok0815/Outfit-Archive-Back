package com.fasthub.backend.admin.product;

import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
import com.fasthub.backend.admin.product.service.ProductService;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImgRepository productImgRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ImgHandler imgHandler;

    @Mock
    private ProductMapper productMapper;

    private Brand buildBrand() {
        return Brand.builder()
                .brandNm("나이키")
                .brandNum("NK-001")
                .build();
    }

    private Product buildProduct(Brand brand) {
        return Product.builder()
                .productNm("에어맥스")
                .productCode("AM-001")
                .productPrice(150000)
                .productQuantity(100)
                .category(ProductCategory.SHOES)
                .brand(brand)
                .build();
    }

    // ────────────────────────────────────────────────
    // 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("등록")
    class Insert {

        @Test
        @DisplayName("성공 - 이미지 없이 등록")
        void insert_success_withoutImage() {
            InsertProductDto dto = new InsertProductDto();
            dto.setBrandId(1L);
            dto.setProductNm("에어맥스");
            dto.setProductCode("AM-001");
            dto.setProductPrice(150000);
            dto.setProductQuantity(100);
            dto.setCategory(ProductCategory.SHOES);

            Brand brand = buildBrand();
            Product product = buildProduct(brand);

            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
            given(productRepository.save(any(Product.class))).willReturn(product);

            productService.insert(dto);

            then(productRepository).should().save(any(Product.class));
            then(productImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 포함 등록")
        void insert_success_withImage() {
            MultipartFile mockFile = mock(MultipartFile.class);
            InsertProductDto dto = new InsertProductDto();
            dto.setBrandId(1L);
            dto.setProductNm("에어맥스");
            dto.setProductCode("AM-001");
            dto.setProductPrice(150000);
            dto.setProductQuantity(100);
            dto.setCategory(ProductCategory.SHOES);
            dto.setImage(List.of(mockFile));

            Brand brand = buildBrand();
            Product product = buildProduct(brand);
            ProductImg productImg = new ProductImg();

            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
            given(productRepository.save(any(Product.class))).willReturn(product);
            given(imgHandler.createImg(eq(mockFile), any(), eq(product))).willReturn(productImg);

            productService.insert(dto);

            then(productImgRepository).should().save(productImg);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 브랜드")
        void insert_fail_brandNotFound() {
            InsertProductDto dto = new InsertProductDto();
            dto.setBrandId(999L);

            given(brandRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.insert(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());

            then(productRepository).should(never()).save(any());
        }
    }

    // ────────────────────────────────────────────────
    // 목록 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("목록 조회")
    class ProductList {

        @Test
        @DisplayName("성공 - 키워드 없이 전체 조회")
        void list_success_noKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            Brand brand = buildBrand();
            Product product = buildProduct(brand);
            ResponseProductDto responseDto = new ResponseProductDto();
            Page<Product> productPage = new PageImpl<>(List.of(product));

            given(productRepository.findAllByKeyword(null, pageable)).willReturn(productPage);
            given(productMapper.productToProductDto(product)).willReturn(responseDto);

            Page<ResponseProductDto> result = productService.list(null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 키워드로 검색")
        void list_success_withKeyword() {
            Pageable pageable = PageRequest.of(0, 10);
            String keyword = "에어맥스";
            Brand brand = buildBrand();
            Product product = buildProduct(brand);
            ResponseProductDto responseDto = new ResponseProductDto();
            Page<Product> productPage = new PageImpl<>(List.of(product));

            given(productRepository.findAllByKeyword(keyword, pageable)).willReturn(productPage);
            given(productMapper.productToProductDto(product)).willReturn(responseDto);

            Page<ResponseProductDto> result = productService.list(keyword, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    // ────────────────────────────────────────────────
    // 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("수정")
    class Update {

        @Test
        @DisplayName("성공 - 이미지 없이 수정")
        void update_success_withoutImage() {
            UpdateProductDto dto = new UpdateProductDto();
            dto.setId(1L);
            dto.setBrandId(1L);
            dto.setProductNm("에어포스");
            dto.setProductCode("AF-001");
            dto.setProductPrice(120000);
            dto.setProductQuantity(50);
            dto.setCategory(ProductCategory.SHOES);

            Brand brand = buildBrand();
            Product product = buildProduct(brand);

            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));

            productService.update(dto);

            then(productImgRepository).should(never()).deleteByProduct(any());
            then(productImgRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 이미지 교체 수정")
        void update_success_withImage() {
            MultipartFile mockFile = mock(MultipartFile.class);
            UpdateProductDto dto = new UpdateProductDto();
            dto.setId(1L);
            dto.setBrandId(1L);
            dto.setProductNm("에어포스");
            dto.setProductCode("AF-001");
            dto.setProductPrice(120000);
            dto.setProductQuantity(50);
            dto.setCategory(ProductCategory.SHOES);
            dto.setImage(List.of(mockFile));

            Brand brand = buildBrand();
            Product product = buildProduct(brand);
            ProductImg productImg = new ProductImg();

            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(brandRepository.findById(1L)).willReturn(Optional.of(brand));
            given(imgHandler.createImg(eq(mockFile), any(), eq(product))).willReturn(productImg);

            productService.update(dto);

            then(productImgRepository).should().deleteByProduct(product);
            then(productImgRepository).should().save(productImg);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void update_fail_productNotFound() {
            UpdateProductDto dto = new UpdateProductDto();
            dto.setId(999L);

            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_UPDATE.getMessage());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 브랜드")
        void update_fail_brandNotFound() {
            UpdateProductDto dto = new UpdateProductDto();
            dto.setId(1L);
            dto.setBrandId(999L);

            Brand brand = buildBrand();
            Product product = buildProduct(brand);

            given(productRepository.findById(1L)).willReturn(Optional.of(product));
            given(brandRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("성공")
        void delete_success() {
            Brand brand = buildBrand();
            Product product = buildProduct(brand);

            given(productRepository.findById(1L)).willReturn(Optional.of(product));

            productService.delete("1");

            then(productRepository).should().delete(product);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 상품")
        void delete_fail_productNotFound() {
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.delete("999"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_FAIL_DELETE.getMessage());

            then(productRepository).should(never()).delete(any());
        }
    }
}
