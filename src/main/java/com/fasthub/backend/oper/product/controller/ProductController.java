package com.fasthub.backend.oper.product.controller;

import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/insert")
    public void insert(ProductDto productDto, List<MultipartFile> images){
        productService.insert(productDto,images);
    }

    @GetMapping("/select")
    public void select(ProductDto productDto){
        productService.select(productDto);
    }

    @GetMapping("/list")
    public void list(ProductDto productDto){
        productService.list(productDto);
    }

    @PutMapping("/update")
    public void update(ProductDto productDto){
        productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public void delete(ProductDto productDto){
        productService.delete(productDto);
    }

}
