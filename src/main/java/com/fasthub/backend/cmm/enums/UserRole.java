package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    NOT_REGISTERED,
    ROLE_USER,
    ROLE_ADMIN;

    public String getValue() {
        return this.name();
    }
    public String getRole(String name){
        return "ROLE_"+name;
    }
}
