package com.fasthub.backend.usr.repository;

import com.fasthub.backend.usr.entity.UsrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UsrRepository extends JpaRepository<UsrEntity, Long> {
}
