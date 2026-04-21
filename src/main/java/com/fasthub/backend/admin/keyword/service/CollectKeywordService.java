package com.fasthub.backend.admin.keyword.service;

import com.fasthub.backend.admin.keyword.dto.InsertKeywordDto;
import com.fasthub.backend.admin.keyword.dto.ResponseKeywordDto;
import com.fasthub.backend.admin.keyword.entity.CollectKeyword;
import com.fasthub.backend.admin.keyword.repository.CollectKeywordRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectKeywordService {

    private final CollectKeywordRepository collectKeywordRepository;

    @Transactional(readOnly = true)
    public List<ResponseKeywordDto> list() {
        return collectKeywordRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public void insert(InsertKeywordDto dto) {
        CollectKeyword keyword = CollectKeyword.builder()
                .keyword(dto.getKeyword())
                .category(dto.getCategory())
                .active(true)
                .build();
        collectKeywordRepository.save(keyword);
    }

    @Transactional
    public void delete(Long id) {
        CollectKeyword keyword = collectKeywordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.KEYWORD_NOT_FOUND));
        keyword.deactivate();
    }

    private ResponseKeywordDto toResponseDto(CollectKeyword keyword) {
        ResponseKeywordDto dto = new ResponseKeywordDto();
        dto.setId(keyword.getId());
        dto.setKeyword(keyword.getKeyword());
        dto.setCategory(keyword.getCategory());
        dto.setCategoryName(keyword.getCategory().getKorName());
        dto.setActive(keyword.isActive());
        dto.setCreatedAt(keyword.getCreatedAt());
        return dto;
    }
}
