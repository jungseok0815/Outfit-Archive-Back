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
            brandImgRepository.deleteByBrand(brand);
            brandImgRepository.save(imgHandler.createImg(updateBrandDto.getBrandImg(), BrandImg::new, brand));
        }
    }

    @Transactional
    public void delete(String brandNo) {
        Brand brand = brandRepository.findById(Long.valueOf(brandNo))
                .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
        brandRepository.delete(brand);
    }
}
