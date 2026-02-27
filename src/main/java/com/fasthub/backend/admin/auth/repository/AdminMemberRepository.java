package com.fasthub.backend.admin.auth.repository;

import com.fasthub.backend.admin.auth.entity.AdminMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminMemberRepository extends JpaRepository<AdminMember, Long> {
    Optional<AdminMember> findByMemberId(String memberId);
    boolean existsByMemberId(String memberId);

    @Query("SELECT a FROM AdminMember a LEFT JOIN FETCH a.brand")
    List<AdminMember> findAllWithBrand();
}
