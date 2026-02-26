package com.fasthub.backend.admin.product.service;

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
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final BrandRepository brandRepository;
    private final ImgHandler imgHandler;
    private final ProductMapper productMapper;

    @Transactional
    public void insert(InsertProductDto productDto) {
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        Product product = productRepository.save(Product.builder()
                .productNm(productDto.getProductNm())
                .productCode(productDto.getProductCode())
                .productPrice(productDto.getProductPrice())
                .productQuantity(productDto.getProductQuantity())
                .category(productDto.getCategory())
                .brand(brand)
                .build());
        if (productDto.getImage() != null) {
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, product)));
        }
    }

    public Page<ResponseProductDto> list(String keyword, Pageable pageable) {
        return productRepository.findAllByKeyword(keyword, pageable)
                .map(productMapper::productToProductDto);
    }

    public Page<ResponseProductDto> listForUser(String keyword, ProductCategory category, Pageable pageable) {
        return productRepository.findAllByKeywordAndCategory(keyword, category, pageable)
                .map(productMapper::productToProductDto);
    }

    @Transactional
    public void update(UpdateProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_UPDATE));
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        product.update(productDto.getProductNm(), productDto.getProductCode(),
                productDto.getProductPrice(), productDto.getProductQuantity(), productDto.getCategory(), brand);
        if (productDto.getImage() != null) {
            productImgRepository.deleteByProduct(product);
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, product)));
        }
    }

    @Transactional
    public void delete(String id) {
        Product product = productRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_DELETE));
        productRepository.delete(product);
    }
}
