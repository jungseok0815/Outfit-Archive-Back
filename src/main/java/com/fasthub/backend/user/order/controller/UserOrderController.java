package com.fasthub.backend.user.order.controller;

import com.fasthub.backend.user.order.dto.InsertUserOrderDto;
import com.fasthub.backend.user.order.dto.ResponseUserOrderDto;
import com.fasthub.backend.user.order.service.UserOrderService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/order")
@RequiredArgsConstructor
public class UserOrderController {

    private final UserOrderService userOrderService;

    @PostMapping
    public ResponseEntity<ResponseUserOrderDto> order(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody InsertUserOrderDto dto) {
        return ResponseEntity.status(201).body(userOrderService.order(userDetails.getId(), dto));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ResponseUserOrderDto>> myOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userOrderService.myOrders(userDetails.getId(), pageable));
    }
}
