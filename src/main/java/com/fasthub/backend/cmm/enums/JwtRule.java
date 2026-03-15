package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum JwtRule {
    JWT_ISSUE_HEADER("Set-Cookie"),
    JWT_RESOLVE_HEADER("Cookie"),
    USER_ACCESS_PREFIX("userAccess"),
    USER_REFRESH_PREFIX("userRefresh"),
    ADMIN_ACCESS_PREFIX("adminAccess"),
    ADMIN_REFRESH_PREFIX("adminRefresh");

    private final String value;
}
