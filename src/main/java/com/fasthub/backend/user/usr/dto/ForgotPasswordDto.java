package com.fasthub.backend.user.usr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordDto {

    @NotBlank
    @Email
    private String email;
}
