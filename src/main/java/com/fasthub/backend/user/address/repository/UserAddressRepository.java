package com.fasthub.backend.user.address.repository;

import com.fasthub.backend.user.address.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);
    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);
    int countByUserId(Long userId);
}
