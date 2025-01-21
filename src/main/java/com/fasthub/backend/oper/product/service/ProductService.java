package com.fasthub.backend.oper.product.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.dto.ResponseProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import com.fasthub.backend.oper.product.repository.ProductImgRepository;
import com.fasthub.backend.oper.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ImgHandler imgHandler;
    @Value("${file.path-product}")
    private String productFilePath;
    private ModelMapper modelMapper;


    public Result insert(InsertProductDto productDto){
        Product product = Product.builder()
                .productNm(productDto.getProductName())
                .category(productDto.getCategory())
                .productPrice(2500)
                .productAuantity(300).build();
        Product productResult =  productRepository.save(product);

        if (!productDto.getImage().isEmpty()){
            productDto.getImage().forEach((item) -> {
                try {
                    String fileName = imgHandler.getFileName(item.getOriginalFilename());
                    String filePath = imgHandler.getFilePath(item,productFilePath,fileName);
                    productImgRepository.save(ProductImg.builder()
                            .imgNm(fileName)
                            .imgPath(filePath)
                            .imgOriginNm(item.getOriginalFilename())
                            .product(Objects.requireNonNull(productResult))
                            .build());
                } catch (IOException e) {
                    throw new BusinessException(ErrorCode.FAIR_CREATE_FILE);
                }
            });
        }
        return Result.success();
    }

    public String select(InsertProductDto productDto, Pageable pageable){
        return null;
    }

    public Result list(){
        List<ResponseProductDto> result = new ArrayList<>();
        productRepository.findAll().forEach((item) ->{
            result.add(new ResponseProductDto(item.getId(),
                    item.getProductNm(),
                    item.getCategory(),
                    item.getProductPrice(),
                    item.getImages(),
                    item.getProductAuantity()
                   ));
        });
        return  Result.success(result);
    }

    public void update(InsertProductDto productDto){

    }

    public void delete(InsertProductDto productDto){

    }
}
