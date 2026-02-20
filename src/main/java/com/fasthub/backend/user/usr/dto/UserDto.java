package com.fasthub.backend.user.usr.dto;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.user.usr.entity.User;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto  {
    private Long id;
    private String userId;
    private String userPwd;
    private String userNm;
    private int userAge;
    private UserRole authName;

    public UserDto(User user){
        this.id = user.getId();
        this.userId = user.getUserId();
        this.userPwd = user.getUserPwd();
        this.userNm = user.getUserNm();
        this.userAge = user.getUserAge();
        this.authName = user.getAuthName();
    }
}
