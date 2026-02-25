package com.fasthub.backend.user.usr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 2, max = 20)
    private String userNm;

    @NotNull
    private int userAge;

    @NotBlank
    @Size(min = 8, max = 16)
    private String userPwd;
}
