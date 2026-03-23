package com.fasthub.backend.admin.banner.controller;

import com.fasthub.backend.admin.banner.dto.InsertBannerDto;
import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.dto.UpdateBannerDto;
import com.fasthub.backend.admin.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseBannerDto>> list() {
        return ResponseEntity.ok(bannerService.list());
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody InsertBannerDto dto) {
        bannerService.insert(dto);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(@RequestBody UpdateBannerDto dto) {
        bannerService.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
