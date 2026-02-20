package com.fasthub.backend.admin.brand.service;

import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.UpdateBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import com.fasthub.backend.admin.brand.entity.BrandImg;
import com.fasthub.backend.admin.brand.mapper.BrandMapper;
import com.fasthub.backend.admin.brand.repository.BrandImgRepository;
import com.fasthub.backend.admin.brand.repository.BrandRepository;
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

    public Page<Brand> list(String keyword, Pageable pageable) {
        return brandRepository.findAllByKeyword(keyword, pageable);
    }

    @Transactional
    public void insert(InsertBrandDto insertBrandDto) {
        BrandImg brandImg = brandImgRepository.save(imgHandler.createImg(insertBrandDto.getBrandImg(), BrandImg::new));
        Brand brand = brandMapper.brandDtoToBrand(insertBrandDto);
        brandRepository.save(brand);
    }

    @Transactional
    public void update(UpdateBrandDto updateBrandDto) {
        brandRepository.findById(updateBrandDto.getId())
                .ifPresent(brand -> {
                    brandImgRepository.findByBrand(brand).ifPresent(brandImg -> {
                    });
                });
    }

    @Transactional
    public void delete(String brandNo) {
        brandRepository.deleteById(Long.valueOf(brandNo));
    }
}
