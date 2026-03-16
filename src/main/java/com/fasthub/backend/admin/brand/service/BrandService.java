package com.fasthub.backend.admin.brand.service;

import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.ResponseBrandDto;
import com.fasthub.backend.admin.brand.dto.UpdateBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.entity.BrandImg;
import com.fasthub.backend.admin.brand.mapper.BrandMapper;
import com.fasthub.backend.admin.brand.repository.BrandImgRepository;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.ImgHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandImgRepository brandImgRepository;
    private final ImgHandler imgHandler;
    private final BrandMapper brandMapper;

    public Page<ResponseBrandDto> list(String keyword, Pageable pageable) {
        return brandRepository.findAllByKeyword(keyword, pageable)
                .map(brandMapper::brandToResponseDto);
    }

    @Transactional
    public void insert(InsertBrandDto insertBrandDto) {
        Brand brand = brandRepository.save(brandMapper.insertDtoToBrand(insertBrandDto));
        if (insertBrandDto.getBrandImg() != null) {
            brandImgRepository.save(imgHandler.createImg(insertBrandDto.getBrandImg(), BrandImg::new, brand));
        }
    }

    @Transactional
    public void update(UpdateBrandDto updateBrandDto) {
        Brand brand = brandRepository.findById(updateBrandDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brand.update(updateBrandDto.getBrandNm(), updateBrandDto.getBrandNum(),
                updateBrandDto.getBrandLocation(), updateBrandDto.getBrandDc());
        if (updateBrandDto.getBrandImg() != null) {
            // 기존 이미지를 S3에서 먼저 삭제 후 DB 엔티티 제거
            brand.getImages().forEach(img -> imgHandler.deleteFile(img.getImgNm()));
            brandImgRepository.deleteByBrand(brand);
            brandImgRepository.save(imgHandler.createImg(updateBrandDto.getBrandImg(), BrandImg::new, brand));
        }
    }

    @Transactional
    public void delete(String brandNo) {
        Brand brand = brandRepository.findById(Long.valueOf(brandNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        // 브랜드 삭제 전 S3 이미지 먼저 제거 (CASCADE ALL로 DB 엔티티는 자동 삭제)
        brand.getImages().forEach(img -> imgHandler.deleteFile(img.getImgNm()));
        brandRepository.delete(brand);
    }
}
