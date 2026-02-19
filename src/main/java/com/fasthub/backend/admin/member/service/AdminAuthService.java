package com.fasthub.backend.admin.member.service;

import com.fasthub.backend.admin.member.dto.AdminLoginDto;
import com.fasthub.backend.admin.member.dto.JoinLoginDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminAuthService {
    public void adminLogin(AdminLoginDto adminLoginDto){
        log.info("admin Info : {}",  adminLoginDto.toString());
    }


    public void adminJoin(JoinLoginDto joinLoginDto){
        log.info("join dto Info : {}",  joinLoginDto.toString());




    }


}
