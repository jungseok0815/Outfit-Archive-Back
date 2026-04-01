package com.fasthub.backend.user.address.controller;

import com.fasthub.backend.user.address.dto.InsertAddressDto;
import com.fasthub.backend.user.address.dto.ResponseAddressDto;
import com.fasthub.backend.user.address.dto.UpdateAddressDto;
import com.fasthub.backend.user.address.service.UserAddressService;
import com.fasthub.backend.user.usr.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usr/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/list")
    public ResponseEntity<List<ResponseAddressDto>> list(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userAddressService.list(userDetails.getId()));
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> insert(@RequestBody @Valid InsertAddressDto dto,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        userAddressService.insert(dto, userDetails.getId());
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid UpdateAddressDto dto,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        userAddressService.update(id, dto, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        userAddressService.delete(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/default/{id}")
    public ResponseEntity<Void> setDefault(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        userAddressService.setDefault(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
