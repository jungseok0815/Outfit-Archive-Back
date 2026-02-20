package com.fasthub.backend.admin.brand.service;

import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
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


    public Result list(String keyword, Pageable pageable){
        Page<Brand> BrandPage =  brandRepository.findAllByKeyword(keyword,pageable);
//        return Result.success(BrandPage.map(brandMapper::brandEntityToResponseBrandDto));
        return null;
    }

    @Transactional
    public Result insert(InsertBrandDto insertBrandDto){
        BrandImg brandImg = brandImgRepository.save(imgHandler.createImg(insertBrandDto.getBrandImg(), BrandImg::new));
        Brand brand = brandMapper.brandDtoToBrand(insertBrandDto);
//        brand.setBrandImg(brandImg);
        brandRepository.save(brand);
        return Result.success("brand insert Ok");
    }

    @Transactional
    public Result update(UpdateBrandDto updateBrandDto){
        brandRepository.findById(updateBrandDto.getId())
                .ifPresent(brand -> {
                    brandImgRepository.findByBrand(brand).ifPresent(brandImg -> {
//                        Brand updateBrand = brandMapper.updateBrandDtoToBrand(updateBrandDto);
//                        brandRepository.save(updateBrand);
                    });
        });
        return Result.success("brand update Ok");
    }

    @Transactional
    public Result delete(String brandNo){
        brandRepository.deleteById(Long.valueOf(brandNo));
        return Result.success("brand delete Ok");
    }


}
