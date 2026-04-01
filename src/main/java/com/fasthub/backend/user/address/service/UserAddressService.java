package com.fasthub.backend.user.address.service;

import com.fasthub.backend.user.address.dto.InsertAddressDto;
import com.fasthub.backend.user.address.dto.ResponseAddressDto;
import com.fasthub.backend.user.address.dto.UpdateAddressDto;
import com.fasthub.backend.user.address.entity.UserAddress;
import com.fasthub.backend.user.address.repository.UserAddressRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final AuthRepository authRepository;

    @Transactional(readOnly = true)
    public List<ResponseAddressDto> list(Long userId) {
        return userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream().map(ResponseAddressDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void insert(InsertAddressDto dto, Long userId) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean makeDefault = dto.isDefault() || userAddressRepository.countByUserId(userId) == 0;

        if (makeDefault) {
            userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(a -> a.setDefault(false));
        }

        userAddressRepository.save(UserAddress.builder()
                .user(user)
                .recipientName(dto.getRecipientName())
                .recipientPhone(dto.getRecipientPhone())
                .zipCode(dto.getZipCode())
                .baseAddress(dto.getBaseAddress())
                .detailAddress(dto.getDetailAddress())
                .isDefault(makeDefault)
                .build());
    }

    @Transactional
    public void update(Long addressId, UpdateAddressDto dto, Long userId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        if (!address.getUser().getId().equals(userId)) throw new IllegalArgumentException("권한이 없습니다.");
        address.update(dto.getRecipientName(), dto.getRecipientPhone(),
                dto.getZipCode(), dto.getBaseAddress(), dto.getDetailAddress());
    }

    @Transactional
    public void delete(Long addressId, Long userId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        if (!address.getUser().getId().equals(userId)) throw new IllegalArgumentException("권한이 없습니다.");

        boolean wasDefault = address.isDefault();
        userAddressRepository.delete(address);

        if (wasDefault) {
            userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                    .stream().findFirst().ifPresent(a -> a.setDefault(true));
        }
    }

    @Transactional
    public void setDefault(Long addressId, Long userId) {
        userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(a -> a.setDefault(false));
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("주소를 찾을 수 없습니다."));
        if (!address.getUser().getId().equals(userId)) throw new IllegalArgumentException("권한이 없습니다.");
        address.setDefault(true);
    }
}
