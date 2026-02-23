package com.fasthub.backend.admin.product.controller;

import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> insert(@ModelAttribute InsertProductDto productDto) {
        productService.insert(productDto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ResponseProductDto>> list(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.list(keyword, pageable));
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@ModelAttribute UpdateProductDto productDto) {
        productService.update(productDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(value = "id") String id) {
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
