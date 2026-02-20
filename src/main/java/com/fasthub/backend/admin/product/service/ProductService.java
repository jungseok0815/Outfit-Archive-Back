package com.fasthub.backend.admin.product.service;

import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.mapper.ProductMapper;
import com.fasthub.backend.admin.product.repository.ProductImgRepository;
import com.fasthub.backend.admin.product.repository.ProductRepository;
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
        Product productResult = productRepository.save(productMapper.InsertproductDtoToProduct(productDto));
        if (productDto.getImage() != null) {
            productDto.getImage().forEach(item ->
                    productImgRepository.save(imgHandler.createImg(item, ProductImg::new, productResult)));
        }
    }

    public Page<ResponseProductDto> list(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.findAllByKeyword(keyword, pageable);
        return productPage.map(productMapper::productToProductDto);
    }

    @Transactional
    public void update(UpdateProductDto productDto) {
        productRepository.findById(productDto.getId())
                .ifPresent(product -> {
                    Product resultProduct = productRepository.save(productMapper.productDtoToProduct(productDto));
                    if (productDto.getImage() != null) {
                        productImgRepository.deleteByProduct(resultProduct);
                        productDto.getImage().forEach(item ->
                                productImgRepository.save(imgHandler.createImg(item, ProductImg::new, resultProduct)));
                    }
                });
    }

    public void delete(String id) {
        productRepository.deleteById(Long.valueOf(id));
    }
}
