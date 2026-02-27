package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.cmm.enums.AdminRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdminJoinDto {
    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자여야 합니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8~100자여야 합니다.")
    private String memberPwd;

    @NotBlank(message = "이름은 필수 입력입니다.")
    @Size(min = 2, max = 20)
    private String memberNm;

    @NotNull(message = "관리자 권한은 필수 입력입니다.")
    private AdminRole adminRole;

    private Long brandId;

}
