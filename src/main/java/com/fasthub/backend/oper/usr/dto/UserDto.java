package com.fasthub.backend.oper.usr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String usrName;
    private String usrEmail;
    private String usrId;
    private String usrPwd;
    private int usrAge;
}
