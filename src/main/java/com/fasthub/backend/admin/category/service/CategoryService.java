package com.fasthub.backend.admin.category.service;

import com.fasthub.backend.admin.category.dto.InsertCategoryDto;
import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.dto.UpdateCategoryDto;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.category.repository.CategoryRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ResponseCategoryDto> list() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public void insert(InsertCategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName().toUpperCase())
                .korName(dto.getKorName())
                .engName(dto.getEngName())
                .defaultSizes(dto.getDefaultSizes())
                .active(true)
                .build();
        categoryRepository.save(category);
    }

    @Transactional
    public void update(UpdateCategoryDto dto) {
        Category category = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.update(dto.getKorName(), dto.getEngName(), dto.getDefaultSizes());
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.deactivate();
    }

    private ResponseCategoryDto toResponseDto(Category category) {
        ResponseCategoryDto dto = new ResponseCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setKorName(category.getKorName());
        dto.setEngName(category.getEngName());
        dto.setDefaultSizes(category.getDefaultSizes());
        dto.setActive(category.isActive());
        return dto;
    }
}
