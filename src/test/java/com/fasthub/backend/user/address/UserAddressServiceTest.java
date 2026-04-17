package com.fasthub.backend.user.address;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.address.dto.InsertAddressDto;
import com.fasthub.backend.user.address.dto.UpdateAddressDto;
import com.fasthub.backend.user.address.entity.UserAddress;
import com.fasthub.backend.user.address.repository.UserAddressRepository;
import com.fasthub.backend.user.address.service.UserAddressService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserAddressService 테스트")
class UserAddressServiceTest {

    @InjectMocks
    private UserAddressService userAddressService;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private AuthRepository authRepository;

    private User buildUser(Long id) {
        return User.builder()
                .id(id)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .build();
    }

    private UserAddress buildAddress(User user, boolean isDefault) {
        UserAddress address = mock(UserAddress.class);
        given(address.getUser()).willReturn(user);
        given(address.isDefault()).willReturn(isDefault);
        return address;
    }

    private InsertAddressDto buildInsertDto(boolean isDefault) {
        InsertAddressDto dto = mock(InsertAddressDto.class);
        given(dto.getRecipientName()).willReturn("홍길동");
        given(dto.getRecipientPhone()).willReturn("010-1234-5678");
        given(dto.getZipCode()).willReturn("12345");
        given(dto.getBaseAddress()).willReturn("서울시");
        given(dto.getDetailAddress()).willReturn("101호");
        given(dto.isDefault()).willReturn(isDefault);
        return dto;
    }

    private UpdateAddressDto buildUpdateDto() {
        UpdateAddressDto dto = mock(UpdateAddressDto.class);
        given(dto.getRecipientName()).willReturn("김철수");
        given(dto.getRecipientPhone()).willReturn("010-9999-8888");
        given(dto.getZipCode()).willReturn("99999");
        given(dto.getBaseAddress()).willReturn("부산시");
        given(dto.getDetailAddress()).willReturn("202호");
        return dto;
    }

    // ────────────────────────────────────────────────
    // 주소 등록
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주소 등록")
    class Insert {

        @Test
        @DisplayName("성공 - 첫 번째 주소는 자동 기본 주소 설정")
        void insert_success_firstAddress_autoDefault() {
            User user = buildUser(1L);
            InsertAddressDto dto = buildInsertDto(false);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(userAddressRepository.countByUserId(1L)).willReturn(0);

            userAddressService.insert(dto, 1L);

            then(userAddressRepository).should().save(any(UserAddress.class));
        }

        @Test
        @DisplayName("성공 - 기본 주소 설정 시 기존 기본 주소 해제")
        void insert_success_setDefault_clearsExisting() {
            User user = buildUser(1L);
            UserAddress existingDefault = mock(UserAddress.class);
            InsertAddressDto dto = buildInsertDto(true);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(userAddressRepository.countByUserId(1L)).willReturn(1);
            given(userAddressRepository.findByUserIdAndIsDefaultTrue(1L)).willReturn(Optional.of(existingDefault));

            userAddressService.insert(dto, 1L);

            then(existingDefault).should().setDefault(false);
            then(userAddressRepository).should().save(any(UserAddress.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void insert_fail_userNotFound() {
            InsertAddressDto dto = buildInsertDto(false);
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userAddressService.insert(dto, 999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ────────────────────────────────────────────────
    // 주소 수정
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주소 수정")
    class Update {

        @Test
        @DisplayName("성공")
        void update_success() {
            User user = buildUser(1L);
            UserAddress address = buildAddress(user, false);
            UpdateAddressDto dto = buildUpdateDto();

            given(userAddressRepository.findById(1L)).willReturn(Optional.of(address));

            userAddressService.update(1L, dto, 1L);

            then(address).should().update(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주소")
        void update_fail_addressNotFound() {
            UpdateAddressDto dto = buildUpdateDto();
            given(userAddressRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userAddressService.update(999L, dto, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void update_fail_unauthorized() {
            User user = buildUser(1L);
            UserAddress address = buildAddress(user, false);
            UpdateAddressDto dto = buildUpdateDto();

            given(userAddressRepository.findById(1L)).willReturn(Optional.of(address));

            assertThatThrownBy(() -> userAddressService.update(1L, dto, 999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ────────────────────────────────────────────────
    // 주소 삭제
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("주소 삭제")
    class Delete {

        @Test
        @DisplayName("성공 - 기본 주소 삭제 시 다음 주소를 기본으로 설정")
        void delete_success_wasDefault() {
            User user = buildUser(1L);
            UserAddress address = buildAddress(user, true);
            UserAddress nextAddress = mock(UserAddress.class);

            given(userAddressRepository.findById(1L)).willReturn(Optional.of(address));
            given(userAddressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(1L))
                    .willReturn(List.of(nextAddress));

            userAddressService.delete(1L, 1L);

            then(userAddressRepository).should().delete(address);
            then(nextAddress).should().setDefault(true);
        }

        @Test
        @DisplayName("성공 - 기본 주소 아니면 그냥 삭제")
        void delete_success_notDefault() {
            User user = buildUser(1L);
            UserAddress address = buildAddress(user, false);

            given(userAddressRepository.findById(1L)).willReturn(Optional.of(address));

            userAddressService.delete(1L, 1L);

            then(userAddressRepository).should().delete(address);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주소")
        void delete_fail_notFound() {
            given(userAddressRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> userAddressService.delete(999L, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void delete_fail_unauthorized() {
            User user = buildUser(1L);
            UserAddress address = buildAddress(user, false);

            given(userAddressRepository.findById(1L)).willReturn(Optional.of(address));

            assertThatThrownBy(() -> userAddressService.delete(1L, 999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
