package com.fasthub.backend.usr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsrDto {
    private Long id;
    private String usrName;
    private String usrEmail;
    private String usrId;
    private String usrPwd;
    private int usrAge;
}
