package com.fasthub.backend.oper.usr.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.usr.dto.JoinDto;
import com.fasthub.backend.oper.usr.service.UsrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UsrController {

    private final UsrService usrService;

    @PostMapping("/insert")
    public Result insert(@RequestBody JoinDto joinDto){
        log.info("joinDto : " + joinDto.toString());
        return usrService.insert(joinDto);
    }

    @GetMapping("/list")
    public void list() {}

    @PutMapping( "/update" )
    public void update(UpdateProductDto productDto){}

    @DeleteMapping("/delete")
    public void delete(){}
}
