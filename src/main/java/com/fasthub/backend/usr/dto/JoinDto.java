package com.fasthub.backend.usr.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JoinDto {
    private String userId;
    private String userPwd;
    private String userNm;
    private String userAge;

}
