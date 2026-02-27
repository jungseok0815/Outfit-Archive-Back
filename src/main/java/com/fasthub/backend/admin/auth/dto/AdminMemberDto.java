package com.fasthub.backend.admin.auth.dto;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.cmm.enums.AdminRole;
import com.fasthub.backend.cmm.enums.UserRole;
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
    private UserRole authName;
    private AdminRole adminRole;

    public AdminMemberDto(AdminMember adminMember) {
        this.id = adminMember.getId();
        this.memberId = adminMember.getMemberId();
        this.memberPwd = adminMember.getMemberPwd();
        this.memberNm = adminMember.getMemberNm();
        this.authName = adminMember.getAuthName();
        this.adminRole = adminMember.getAdminRole();
    }
}
