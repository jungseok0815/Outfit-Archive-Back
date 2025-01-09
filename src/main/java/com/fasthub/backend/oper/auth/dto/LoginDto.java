package com.fasthub.backend.oper.auth.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class LoginDto {
    private String userId;
    private String userPwd;

}
