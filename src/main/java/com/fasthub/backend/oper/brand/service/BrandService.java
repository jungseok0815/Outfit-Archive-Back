package com.fasthub.backend.oper.brand.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.cmm.img.BaseImg;
import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.brand.brandMapper.BrandMapper;
import com.fasthub.backend.oper.brand.dto.BrandDto;
import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.dto.UpdateBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import com.fasthub.backend.oper.brand.entity.BrandImg;
import com.fasthub.backend.oper.brand.repository.BrandImgRepository;
import com.fasthub.backend.oper.brand.repository.BrandRepository;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandImgRepository brandImgRepository;
    private final ImgHandler imgHandler;
    private final BrandMapper brandMapper;

    public Result insert(InsertBrandDto insertBrandDto){
        log.info("insertDtoNM : " + insertBrandDto.getBrandNm());
        Brand testBrand = brandMapper.brandDtoToBrand(insertBrandDto);
        log.info("testBrnad : " + testBrand.getBrandNm());
        log.info("testBrnad : " + testBrand.getBrandDc());
        log.info("testBrnad : " + testBrand.getBrandLocation());
        log.info("testBrnad : " + testBrand.getBrandNum());

        Brand resultBrand = brandRepository.save(brandMapper.brandDtoToBrand(insertBrandDto));
        if (insertBrandDto.getBrandImg() != null){
            brandImgRepository.save(imgHandler.createImg(insertBrandDto.getBrandImg(), BrandImg::new, resultBrand));
        }
        return Result.success("brand insert Ok");
    }

    public Result update(UpdateBrandDto updateBrandDto){
        brandRepository.findById(updateBrandDto.getId())
                .ifPresent(brand -> {
                    brandRepository.save(brandMapper.bradndDtoToBrand(updateBrandDto));
                if (updateBrandDto.getBrandImg() != null){
                    brandImgRepository.deleteByBrand(brand);
                    brandImgRepository.save(imgHandler.createImg(updateBrandDto.getBrandImg(), BrandImg::new, brand));
                }
        });
        return Result.success("brand update Ok");
    }

    public Result delete(String brandNo){
        return null;
    }

    public Result list(){
        return null;
    }

}
