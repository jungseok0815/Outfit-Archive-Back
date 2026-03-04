package com.fasthub.backend.admin.revenue.controller;

import com.fasthub.backend.admin.order.dto.RevenueByBrandDto;
import com.fasthub.backend.admin.revenue.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    @GetMapping("/stats")
    public ResponseEntity<List<RevenueByBrandDto>> stats(
            @RequestParam(value = "brandId", required = false) Long brandId) {
        return ResponseEntity.ok(revenueService.getRevenue(brandId));
    }
}
