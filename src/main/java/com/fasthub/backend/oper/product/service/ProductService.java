package com.fasthub.backend.oper.product.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import com.fasthub.backend.oper.product.repository.ProductImgRepository;
import com.fasthub.backend.oper.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ImgHandler imgHandler;
    @Value("${file.path-product}")
    private String productFilePath;


    public void insert(InsertProductDto productDto){
        Product product = Product.builder()
                .productNm(productDto.getProductName())
                .category(productDto.getCategory())
                .productPrice(2500)
                .productAuantity(300).build();
        log.info("product : " + product);
        Product result =  productRepository.save(product);
        if (!productDto.getImage().isEmpty()){
            productDto.getImage().forEach((item) -> {
                try {
                    String fileName = imgHandler.getFileName(item.getOriginalFilename());
                    String filePath = imgHandler.getFilePath(productFilePath,fileName);
                    ProductImg productImg = ProductImg.builder()
                            .imgNm(fileName)
                            .imgPath(filePath)
                            .product(result)
                            .build();
                    log.info("productImg : " + productImg);
                    productImgRepository.save(productImg);
                } catch (IOException e) {
                    throw new BusinessException(ErrorCode.FAIR_CREATE_FILE);
                }
            });
        }
    }
    public String select(InsertProductDto productDto, Pageable pageable){

        return null;
    }

//    public List<Product> list(ProductDto productDto, Pageable Pageable){
//        Slice<Product> slices = productRepository.findSliceBy(Pageable);
//        return slices.getContent();
//    }

    public void update(InsertProductDto productDto){

    }

    public void delete(InsertProductDto productDto){

    }
}
