package com.fasthub.backend.oper.brand.contoller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.brand.dto.BrandDto;
import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brand")
@RequiredArgsConstructor
public class BrandContoller {

    private final BrandService brandService;

    @PostMapping("/insert")
    public Result insert(InsertBrandDto insertBrandDto){
        return brandService.insert(insertBrandDto);
    }

    @PutMapping("/update")
    public Result update(BrandDto brandDto){
        return brandService.update(brandDto);
    }

    @DeleteMapping("/delete")
    public Result delete(String brandNo){
        return brandService.delete(brandNo);
    }

    @GetMapping("/list")
    public Result list(){
        return brandService.list();
    }
}
