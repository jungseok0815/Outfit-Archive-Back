package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.cmm.enums.AdminRole;
import lombok.Getter;

@Getter
public class AdminLoginResponseDto {
    private final Long id;
    private final String memberId;
    private final String memberNm;
    private final Long brandId;
    private final String brandNm;
    private final AdminRole adminRole;

    public AdminLoginResponseDto(AdminMember adminMember) {
        this.id = adminMember.getId();
        this.memberId = adminMember.getMemberId();
        this.memberNm = adminMember.getMemberNm();
        this.brandId = adminMember.getBrand() != null ? adminMember.getBrand().getId() : null;
        this.brandNm = adminMember.getBrand() != null ? adminMember.getBrand().getBrandNm() : null;
        this.adminRole = adminMember.getAdminRole();
    }
}
