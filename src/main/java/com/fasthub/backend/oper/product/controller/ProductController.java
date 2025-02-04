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
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/list")
    public Result list(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        keyword = keyword.isEmpty() ? null : keyword;
        return productService.list(keyword, pageable);
    }

    @PutMapping(value = "/update" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result update(UpdateProductDto productDto){
        return productService.update(productDto);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam(value="id") String id){
        productService.delete(id);
    }

}
