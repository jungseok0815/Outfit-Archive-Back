package com.fasthub.backend.cmm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", "Invalid Method"),
    ENTITY_NOT_FOUND(400, "C003", "Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),
    INVALID_TYPE_VALUE(400, "C005", "Invalid Type Value"),

    // Member

    NOT_AUTHENTICATED_USER(500, "M003", "사용자의 권한이 인증되지 않음"),
    ID_NOT_FOUND(400, "M001","사용자의 아이디를 찾을 수 없습니다."),
    PWD_NOT_FOUND(400,"M002","사용자의 패스워드를 찾을 수 없습니다."),

    // Coupon
    COUPON_ALREADY_USE(400, "CP001", "Coupon was already used"),
    COUPON_EXPIRE(400, "CP002", "Coupon was already expired"),
    NOT_REFRESG_KEY(400, "CP003", "리프레쉬 키 해석 실패");



    private final int status;
    private final String code;
    private final String message;
}
