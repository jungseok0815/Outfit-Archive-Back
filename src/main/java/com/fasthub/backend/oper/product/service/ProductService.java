package com.fasthub.backend.oper.product.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.ResponseProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import com.fasthub.backend.oper.product.repository.ProductImgRepository;
import com.fasthub.backend.oper.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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
                .productNm(productDto.getProductNm())
                .productCode(productDto.getProductCode())
                .productPrice(productDto.getProductPrice())
                .productBrand(productDto.getProductBrand())
                .productQuantity(productDto.getProductQuantity())
                .category(productDto.getCategory())
                .build();
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
            result.add(new ResponseProductDto(
                    item.getId(),
                    item.getProductNm(),
                    item.getProductCode(),
                    item.getProductPrice(),
                    item.getProductQuantity(),
                    item.getProductBrand(),
                    item.getCategory(),
                    item.getImages()
                   ));
        });
        return  Result.success(result);
    }

    public void update(UpdateProductDto productDto){
        Optional<Product> product = productRepository.findById(productDto.getId());

//        Product product = Product.builder()
//                .productNm(productDto.getProductNm())
//                .productCode(productDto.getProductCode())
//                .productPrice(productDto.getProductPrice())
//                .productBrand(productDto.getProductBrand())
//                .productQuantity(productDto.getProductQuantity())
//                .category(productDto.getCategory())
//                .build();
//        Product productResult =  productRepository.(product);
    }

    public void delete(InsertProductDto productDto){

    }
}
