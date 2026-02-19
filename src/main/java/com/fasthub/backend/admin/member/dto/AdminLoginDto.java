package com.fasthub.backend.admin.member.dto;

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
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 16, message = "아이디는 4~20자여야 합니다.")
    private String memberPwd;
}
