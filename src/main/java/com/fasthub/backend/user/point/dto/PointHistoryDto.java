package com.fasthub.backend.user.point.dto;

import com.fasthub.backend.user.point.entity.PointHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryDto {
    private Long id;
    private int amount;
    private int balanceAfter;
    private String description;
    private PointHistory.PointType type;
    private LocalDateTime createdAt;

    public static PointHistoryDto of(PointHistory history) {
        return PointHistoryDto.builder()
                .id(history.getId())
                .amount(history.getAmount())
                .balanceAfter(history.getBalanceAfter())
                .description(history.getDescription())
                .type(history.getType())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
