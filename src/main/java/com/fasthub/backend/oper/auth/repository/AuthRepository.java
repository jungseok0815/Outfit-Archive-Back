package com.fasthub.backend.oper.auth.repository;

import com.fasthub.backend.oper.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}
