package com.fasthub.backend.oper.product.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.*;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import com.fasthub.backend.oper.product.mapper.ProductMapper;
import com.fasthub.backend.oper.product.repository.ProductImgRepository;
import com.fasthub.backend.oper.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ImgHandler imgHandler;
    private final ProductMapper productMapper;
    @Value("${file.path-product}")
    private String productFilePath;

    @Transactional
    public Result insert(InsertProductDto productDto){
        Product productResult =  productRepository.save(productMapper.InsertproductDtoToProduct(productDto));
        if (productDto.getImage() != null){
            productDto.getImage().forEach((item) -> productImgRepository.save(imgHandler.createImg(item, ProductImg::new, productResult)));
        }
        return Result.success("insert ok");
    }

    public Result list(String keyword, Pageable pageable){
        Page<Product> productPage = productRepository.findAllByKeyword(keyword,pageable);
        return Result.success(productPage.map(productMapper::productToProductDto));
    }

    @Transactional
    public Result update(UpdateProductDto productDto){
        productRepository.findById(productDto.getId())
                .ifPresent(product -> {
                    Product resultProduct =  productRepository.save(productMapper.productDtoToProduct(productDto));
                    if (productDto.getImage() != null){
                        productImgRepository.deleteByProduct(resultProduct);
                        productDto.getImage().forEach((item) -> productImgRepository.save(imgHandler.createImg(item,ProductImg::new,resultProduct)));
                    }
                });
        return Result.success("update ok");
    }

    public Result delete(String id){
        productRepository.deleteById(Long.valueOf(id));
        return Result.success("delete ok");
    }

}
