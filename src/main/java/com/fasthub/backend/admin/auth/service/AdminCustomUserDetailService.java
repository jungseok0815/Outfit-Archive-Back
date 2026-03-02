package com.fasthub.backend.admin.auth.service;

import com.fasthub.backend.admin.auth.dto.AdminCustomUserDetails;
import com.fasthub.backend.admin.auth.dto.AdminMemberDto;
import com.fasthub.backend.admin.auth.entity.AdminMember;
import com.fasthub.backend.admin.auth.repository.AdminMemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class AdminCustomUserDetailService implements UserDetailsService {

    private final AdminMemberRepository adminMemberRepository;

    @Override
    public AdminCustomUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        AdminMember adminMember = adminMemberRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 관리자가 없습니다."));

        log.info("adminCustomUserDetailArea : " + adminMember.getMemberId());

        AdminMemberDto dto = new AdminMemberDto(adminMember);

        return new AdminCustomUserDetails(dto);
    }
}
