package com.fasthub.backend.admin.order.controller;

import com.fasthub.backend.admin.order.dto.ResponseOrderDto;
import com.fasthub.backend.admin.order.dto.UpdateOrderStatusDto;
import com.fasthub.backend.admin.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<Page<ResponseOrderDto>> list(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.list(keyword, pageable));
    }

    @PutMapping("/status")
    public ResponseEntity<Void> updateStatus(@RequestBody UpdateOrderStatusDto dto) {
        orderService.updateStatus(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(value = "id") Long id) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
