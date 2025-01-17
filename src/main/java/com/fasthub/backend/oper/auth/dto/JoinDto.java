package com.fasthub.backend.oper.auth.dto;

import com.fasthub.backend.cmm.enums.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
