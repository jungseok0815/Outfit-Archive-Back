package com.fasthub.backend.user.payment.controller;

import com.fasthub.backend.user.payment.dto.PaymentConfirmRequestDto;
import com.fasthub.backend.user.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usr/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 승인 - 프론트에서 토스 결제 후 paymentKey, orderId, amount 전달
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody PaymentConfirmRequestDto dto) {
        paymentService.confirm(dto);
        return ResponseEntity.ok().build();
    }

    // 결제 취소
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancel(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "고객 요청 취소") String cancelReason) {
        paymentService.cancel(orderId, cancelReason);
        return ResponseEntity.ok().build();
    }
}
