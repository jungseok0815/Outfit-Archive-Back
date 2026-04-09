package com.fasthub.backend.user.usr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ResetPasswordDto {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String newPassword;
}
