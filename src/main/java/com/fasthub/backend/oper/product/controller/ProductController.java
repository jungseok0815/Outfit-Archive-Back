package com.fasthub.backend.oper.product.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping("/insert")
    public Result insert(InsertProductDto productDto){
        return productService.insert(productDto);
    }

    @GetMapping("/select")
    public Result select(InsertProductDto productDto, Pageable pageable){
        return Result.success("success", productService.select(productDto, pageable));
    }
    @GetMapping("/list")
    public Result list( @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword){
        keyword = keyword.isEmpty() ? null : keyword;
//        Pageable pageRequest = PageRequest.of(0,10, Sort.by("id").ascending());
        return productService.list(keyword);
    }

    @PutMapping(value = "/update" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void update(ProductDto productDto){
        productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam(value="id") String id){
        productService.delete(id);
    }

}
