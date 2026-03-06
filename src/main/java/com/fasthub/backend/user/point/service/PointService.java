package com.fasthub.backend.user.point.service;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.point.dto.PointHistoryDto;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final AuthRepository authRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public int getPoint(Long userId) {
        return authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getPoint();
    }

    public Page<PointHistoryDto> getHistory(Long userId, Pageable pageable) {
        User user = authRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return pointHistoryRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(PointHistoryDto::of);
    }
}
