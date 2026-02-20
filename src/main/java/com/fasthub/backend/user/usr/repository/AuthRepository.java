package com.fasthub.backend.user.usr.repository;

import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}
