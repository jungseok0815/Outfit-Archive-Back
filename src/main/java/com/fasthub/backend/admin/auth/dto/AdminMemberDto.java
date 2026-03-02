package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.cmm.enums.AdminRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminMemberDto {
    private Long id;
    private String memberId;
    private String memberPwd;
    private String memberNm;
    private Long brandId;
    private String brandNm;
    private AdminRole adminRole;

    public AdminMemberDto(AdminMember adminMember) {
        this.id = adminMember.getId();
        this.memberId = adminMember.getMemberId();
        this.memberPwd = adminMember.getMemberPwd();
        this.memberNm = adminMember.getMemberNm();
        this.brandId = adminMember.getBrand() != null ? adminMember.getBrand().getId() : null;
        this.brandNm = adminMember.getBrand() != null ? adminMember.getBrand().getBrandNm() : null;
        this.adminRole = adminMember.getAdminRole();
    }
}
