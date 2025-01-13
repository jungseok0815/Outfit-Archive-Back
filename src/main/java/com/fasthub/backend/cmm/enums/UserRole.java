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
    ADMIN;

    public String getValue() {
        return this.name();
    }
}
