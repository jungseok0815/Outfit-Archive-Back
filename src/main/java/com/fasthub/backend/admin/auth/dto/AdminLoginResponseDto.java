package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.cmm.enums.AdminRole;
import lombok.Getter;

@Getter
public class AdminLoginResponseDto {
    private final Long id;
    private final String memberId;
    private final String memberNm;
    private final AdminRole adminRole;

    public AdminLoginResponseDto(AdminMember adminMember) {
        this.id = adminMember.getId();
        this.memberId = adminMember.getMemberId();
        this.memberNm = adminMember.getMemberNm();
        this.adminRole = adminMember.getAdminRole();
    }
}
