package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MANAGER;

    public String getAuthority() {
        return "ROLE_"+this.name();
    }

}
