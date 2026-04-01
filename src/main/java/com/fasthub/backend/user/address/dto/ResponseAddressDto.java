package com.fasthub.backend.user.address.dto;

import com.fasthub.backend.user.address.entity.UserAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseAddressDto {
    private Long id;
    private String recipientName;
    private String recipientPhone;
    private String zipCode;
    private String baseAddress;
    private String detailAddress;
    private boolean isDefault;

    public ResponseAddressDto(UserAddress address) {
        this.id = address.getId();
        this.recipientName = address.getRecipientName();
        this.recipientPhone = address.getRecipientPhone();
        this.zipCode = address.getZipCode();
        this.baseAddress = address.getBaseAddress();
        this.detailAddress = address.getDetailAddress();
        this.isDefault = address.isDefault();
    }
}
