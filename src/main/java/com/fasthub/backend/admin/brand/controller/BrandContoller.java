package com.fasthub.backend.admin.brand.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.UpdateBrandDto;
import com.fasthub.backend.admin.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
@Slf4j
public class BrandContoller {

    private final BrandService brandService;

    @GetMapping("/list")
    public Result list(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                       @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return brandService.list(keyword, null);
    }

    @PostMapping("/insert")
    public Result insert(InsertBrandDto insertBrandDto){
        return brandService.insert(insertBrandDto);
    }

    @PutMapping("/update")
    public Result update(UpdateBrandDto brandDto){
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public Result delete(String brandNo){
        return brandService.delete(brandNo);
    }


}
