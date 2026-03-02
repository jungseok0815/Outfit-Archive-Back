package com.fasthub.backend.admin.auth.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class AdminLoginDto {
    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자여야 합니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8~100자여야 합니다.")
    private String memberPwd;
}
