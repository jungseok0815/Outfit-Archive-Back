package com.fasthub.backend.admin.banner.controller;

import com.fasthub.backend.admin.banner.dto.InsertBannerDto;
import com.fasthub.backend.admin.banner.dto.ResponseBannerDto;
import com.fasthub.backend.admin.banner.dto.UpdateBannerDto;
import com.fasthub.backend.admin.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> insert(@ModelAttribute InsertBannerDto dto) {
        bannerService.insert(dto);
        return ResponseEntity.status(201).build();
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@ModelAttribute UpdateBannerDto dto) {
        bannerService.update(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
        bannerService.delete(id);
        return ResponseEntity.ok().build();
    }
}
