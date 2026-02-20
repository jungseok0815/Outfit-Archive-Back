package com.fasthub.backend.user.usr.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.user.usr.dto.JoinDto;
import com.fasthub.backend.user.usr.service.UsrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr")
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
