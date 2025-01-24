package com.fasthub.backend.oper.brand.service;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.brand.dto.BrandDto;
import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    public Result insert(InsertBrandDto insertBrandDto){
        return null;
    }

    public Result update(BrandDto brandDto){
        return null;
    }

    public Result delete(String brandNo){
        return null;
    }

    public Result list(){
        return null;
    }
}
