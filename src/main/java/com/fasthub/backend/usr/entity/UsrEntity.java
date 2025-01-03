package com.fasthub.backend.usr.entity;

import com.fasthub.backend.usr.dto.JoinDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Stack;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UsrEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String usrId;

    @Column(name = "usr_pwd", nullable = false, unique = true, length = 30)
    private String usrPwd;

    @Column(name = "usr_name", nullable = false)
    private String usrName;

    @Column(name = "usr_age", nullable = false)
    private int usrAge;

    public static UsrEntity mappingUsrEntity(JoinDto joinDto){
        UsrEntity usrEntity = new UsrEntity();
        return usrEntity;
    }
}

