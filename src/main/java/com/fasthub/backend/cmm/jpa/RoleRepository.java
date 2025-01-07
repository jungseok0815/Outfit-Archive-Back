package com.fasthub.backend.cmm.jpa;

import com.fasthub.backend.cmm.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<UserRoleEntity, Long> {
    boolean existsByName(UserRole name);
}
