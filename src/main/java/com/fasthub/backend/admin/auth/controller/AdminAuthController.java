package com.fasthub.backend.admin.auth.controller;

import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@Slf4j
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    public void adminLogin(@RequestBody @Valid AdminLoginDto adminLoginDto){
        log.info("adminLoginDto : {}" , adminLoginDto.toString());
        adminAuthService.adminLogin(adminLoginDto);
    }

    @PostMapping("/join")
    public void adminJoin(@RequestBody @Valid AdminJoinDto joinLoginDto){
        log.info("joinLoginDto : {}" , joinLoginDto.toString());
        adminAuthService.adminJoin(joinLoginDto);
    }

}
