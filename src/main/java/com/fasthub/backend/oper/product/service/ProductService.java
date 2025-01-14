package com.fasthub.backend.oper.product.service;

import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import com.fasthub.backend.oper.product.repository.ProductImgRepository;
import com.fasthub.backend.oper.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImgRepository productImgRepository;
    private final ImgHandler imgHandler;


    public void insert(ProductDto productDto, List<MultipartFile> images){
        Product product = Product.builder().productNm(productDto.getProductNm())
                .category(productDto.getProductCategory()).build();
        productRepository.save(product);

        if (!images.isEmpty()){
            images.forEach((item) -> {
                System.out.println("item : " + item);
                String fileName = imgHandler.getFileName(item.getOriginalFilename());
                String filePath = imgHandler.getFilePath(fileName);

            });

        }
    }

    public void select(ProductDto productDto){
    }

    public void list(ProductDto productDto){

    }

    public void update(ProductDto productDto){

    }

    public void delete(ProductDto productDto){

    }
}
