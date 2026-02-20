package com.fasthub.backend.admin.auth.controller;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginResponseDto;
import com.fasthub.backend.admin.auth.service.AdminAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> adminLogin(@RequestBody @Valid AdminLoginDto adminLoginDto) {
        return ResponseEntity.ok(adminAuthService.adminLogin(adminLoginDto));
    }

    @PostMapping("/join")
    public ResponseEntity<Void> adminJoin(@RequestBody @Valid AdminJoinDto joinLoginDto) {
        adminAuthService.adminJoin(joinLoginDto);
        return ResponseEntity.status(201).build();
    }
}
