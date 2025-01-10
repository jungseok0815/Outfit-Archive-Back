package com.fasthub.backend.oper.auth.dto;

import com.fasthub.backend.oper.auth.entity.User;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto extends User {
    private Long id;
    private String userId;
    private String userPwd;
    private String userNm;
    private int userAge;
    private String role;
}
