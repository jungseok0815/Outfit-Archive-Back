package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminRole {

    SUPER_ADMIN("최고 관리자", "모든 권한 보유", 1),
    ADMIN("관리자", "일반 관리 권한 보유", 2),
    PARTNER("협력 업체", "제한된 관리 권한 보유", 3);

    private final String korName;     // 한글 명칭
    private final String description; // 권한 설명
    private final int level;          // 권한 레벨 (숫자가 낮을수록 상위 권한)

    /**
     * 현재 권한이 대상 권한보다 상위인지 확인
     * ex) SUPER_ADMIN.isHigherThan(ADMIN) → true
     */
    public boolean isHigherThan(AdminRole other) {
        return this.level < other.level;
    }

    /**
     * 현재 권한이 대상 권한 이상인지 확인 (같은 레벨 포함)
     * ex) ADMIN.isHigherOrEqualTo(ADMIN) → true
     */
    public boolean isHigherOrEqualTo(AdminRole other) {
        return this.level <= other.level;
    }
}
