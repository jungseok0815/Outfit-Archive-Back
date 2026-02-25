package com.fasthub.backend.user.usr.repository;

import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    boolean existsByUserId(String userId);

    @Query("SELECT u FROM User u WHERE :keyword IS NULL OR :keyword = '' OR u.userNm LIKE %:keyword%")
    Page<User> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
