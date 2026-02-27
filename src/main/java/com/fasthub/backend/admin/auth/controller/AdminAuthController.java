package com.fasthub.backend.admin.auth.controller;

import com.fasthub.backend.admin.auth.dto.AdminJoinDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginDto;
import com.fasthub.backend.admin.auth.dto.AdminLoginResponseDto;
import com.fasthub.backend.admin.auth.dto.AdminMemberResponseDto;
import com.fasthub.backend.admin.auth.service.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> adminLogin(
            @RequestBody @Valid AdminLoginDto adminLoginDto,
            HttpServletResponse response) {
        return ResponseEntity.ok(adminAuthService.adminLogin(adminLoginDto, response));
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/join")
    public ResponseEntity<Void> adminJoin(@RequestBody @Valid AdminJoinDto joinLoginDto) {
        adminAuthService.adminJoin(joinLoginDto);
        return ResponseEntity.status(201).build();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/membersList")
    public ResponseEntity<List<AdminMemberResponseDto>> getAdminList() {
        return ResponseEntity.ok(adminAuthService.getAdminList());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminAuthService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
