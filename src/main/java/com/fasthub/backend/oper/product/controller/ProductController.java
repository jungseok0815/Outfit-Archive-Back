package com.fasthub.backend.oper.product.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

//    @PostMapping("/insert")
//    public void insert(ProductDto productDto, List<MultipartFile> images){
//
//
//
//
//        productService.insert(productDto,images);
//    }

    @PostMapping("/insert")
    public void insert(@RequestParam(value="category") String category, @RequestParam(value="productName") String productName, @RequestParam(value="price") String price,
                       @RequestParam(value="img") MultipartFile img, @RequestParam(value="description") String description){
        log.info("category : " + category);
        log.info("productName : " + productName);
        log.info("price : " + price);
        log.info("img : " + img);
        log.info("description : " + description);

    }

    @GetMapping("/select")
    public Result select(ProductDto productDto, Pageable pageable){
        PageRequest pageRequest = PageRequest.of(0,10, Sort.by("id").ascending());
        return Result.success("success", productService.select(productDto, pageable));
    }

    @GetMapping("/list")
    public Result list(ProductDto productDto, Pageable Pageable){
        Pageable pageRequest = PageRequest.of(0,10, Sort.by("id").ascending());
        return Result.success("success", productService.list(productDto,pageRequest));
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
