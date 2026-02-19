package com.fasthub.backend.admin.member.contoller;

import com.fasthub.backend.admin.member.dto.AdminLoginDto;
import com.fasthub.backend.admin.member.dto.JoinLoginDto;
import com.fasthub.backend.admin.member.service.AdminAuthService;
import com.fasthub.backend.oper.usr.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/admin/auth")
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
    public void adminJoin(@RequestBody @Valid JoinLoginDto joinLoginDto){
        log.info("joinLoginDto : {}" , joinLoginDto.toString());
        adminAuthService.adminJoin(joinLoginDto);
    }

}
