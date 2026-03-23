package com.fasthub.backend.user.banner.controller;

import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usr/banner")
@RequiredArgsConstructor
public class UserBannerController {

    private final BannerService bannerService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseBannerDto>> list() {
        return ResponseEntity.ok(bannerService.listActive());
    }
}
