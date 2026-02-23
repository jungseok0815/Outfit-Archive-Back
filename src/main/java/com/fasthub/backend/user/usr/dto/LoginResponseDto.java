package com.fasthub.backend.user.usr.dto;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.usr.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final Long id;
    private final String userId;
    private final String userNm;
    private final int userAge;
    private final UserRole authName;

    public LoginResponseDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.userNm = user.getUserNm();
        this.userAge = user.getUserAge();
        this.authName = user.getAuthName();
    }
}
