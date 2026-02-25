package com.fasthub.backend.user.usr.dto;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.usr.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseUserDto {

    private Long id;
    private String userId;
    private String userNm;
    private int userAge;
    private UserRole authName;

    public ResponseUserDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.userNm = user.getUserNm();
        this.userAge = user.getUserAge();
        this.authName = user.getAuthName();
    }
}
