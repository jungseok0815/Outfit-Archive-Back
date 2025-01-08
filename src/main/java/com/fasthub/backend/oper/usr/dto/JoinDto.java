package com.fasthub.backend.oper.usr.dto;

import com.fasthub.backend.cmm.enums.UserRole;
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
    private int userAge;
    private String authName;

}
