package com.fasthub.backend.admin.auth.repository;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<AdminMember, Long> {
    Optional<AdminMember> findByMemberId(String memberId);
    boolean existsByMemberId(String memberId);
}
