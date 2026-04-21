package com.fasthub.backend.admin.category.controller;

import com.fasthub.backend.admin.category.dto.InsertCategoryDto;
import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.dto.UpdateCategoryDto;
import com.fasthub.backend.admin.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseCategoryDto>> list() {
        return ResponseEntity.ok(categoryService.list());
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody InsertCategoryDto dto) {
        categoryService.insert(dto);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody UpdateCategoryDto dto) {
        categoryService.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
