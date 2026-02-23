package com.fasthub.backend.admin.product.service;

import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
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
    private final ImgHandler imgHandler;
    private final ProductMapper productMapper;

    @Transactional
    public void insert(InsertProductDto productDto) {
        Product product = productRepository.save(productMapper.insertProductDtoToProduct(productDto));
        if (productDto.getImage() != null) {
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, product)));
        }
    }

    public Page<ResponseProductDto> list(String keyword, Pageable pageable) {
        return productRepository.findAllByKeyword(keyword, pageable)
                .map(productMapper::productToProductDto);
    }

    @Transactional
    public void update(UpdateProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FAIL_UPDATE));
        product.update(productDto.getProductNm(), productDto.getProductCode(),
                productDto.getProductPrice(), productDto.getProductQuantity(), productDto.getCategory());
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
