package com.fasthub.backend.user.usr.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class LoginDto {
    private String userId;
    private String userPwd;

}
