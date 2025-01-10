package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenStatus {
    AUTHENTICATED, //인증 상태
    EXPIRED, // 만료 상태
    INVALID // 유효하지 않은 상태
}
