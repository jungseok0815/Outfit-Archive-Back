package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.cmm.enums.AdminRole;
import lombok.Getter;

@Getter
public class AdminMemberResponseDto {
    private final Long id;
    private final String memberId;
    private final String memberNm;
    private final String affiliation;
    private final AdminRole adminRole;

    public AdminMemberResponseDto(AdminMember adminMember) {
        this.id = adminMember.getId();
        this.memberId = adminMember.getMemberId();
        this.memberNm = adminMember.getMemberNm();
        this.affiliation = adminMember.getAffiliation();
        this.adminRole = adminMember.getAdminRole();
    }
}
