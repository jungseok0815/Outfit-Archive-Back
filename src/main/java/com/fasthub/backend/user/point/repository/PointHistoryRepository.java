package com.fasthub.backend.user.point.repository;

import com.fasthub.backend.user.point.entity.PointHistory;
import com.fasthub.backend.user.usr.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
