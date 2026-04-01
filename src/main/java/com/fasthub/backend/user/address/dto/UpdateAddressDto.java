package com.fasthub.backend.user.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAddressDto {
    @NotBlank private String recipientName;
    @NotBlank private String recipientPhone;
    @NotBlank private String zipCode;
    @NotBlank private String baseAddress;
    private String detailAddress;
}
