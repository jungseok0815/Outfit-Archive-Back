package com.fasthub.backend.user.point;

import com.fasthub.backend.cmm.enums.UserRole;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import com.fasthub.backend.user.point.dto.PointHistoryDto;
import com.fasthub.backend.user.point.entity.PointHistory;
import com.fasthub.backend.user.point.repository.PointHistoryRepository;
import com.fasthub.backend.user.point.service.PointService;
import com.fasthub.backend.user.usr.entity.User;
import com.fasthub.backend.user.usr.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 테스트")
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    private User buildUser(int point) {
        return User.builder()
                .id(1L)
                .userId("user01")
                .userNm("홍길동")
                .userPwd("encodedPwd")
                .userAge(25)
                .authName(UserRole.ROLE_USER)
                .point(point)
                .build();
    }

    // ────────────────────────────────────────────────
    // 포인트 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("포인트 조회")
    class GetPoint {

        @Test
        @DisplayName("성공")
        void getPoint_success() {
            User user = buildUser(5000);
            given(authRepository.findById(1L)).willReturn(Optional.of(user));

            int result = pointService.getPoint(1L);

            assertThat(result).isEqualTo(5000);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void getPoint_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.getPoint(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    // ────────────────────────────────────────────────
    // 포인트 내역 조회
    // ────────────────────────────────────────────────
    @Nested
    @DisplayName("포인트 내역 조회")
    class GetHistory {

        @Test
        @DisplayName("성공")
        void getHistory_success() {
            User user = buildUser(5000);
            Pageable pageable = PageRequest.of(0, 10);
            PointHistory history = mock(PointHistory.class);
            given(history.getAmount()).willReturn(1000);
            given(history.getBalanceAfter()).willReturn(5000);
            given(history.getDescription()).willReturn("구매 적립");
            given(history.getType()).willReturn(PointHistory.PointType.EARN);

            given(authRepository.findById(1L)).willReturn(Optional.of(user));
            given(pointHistoryRepository.findByUserOrderByCreatedAtDesc(eq(user), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(history)));

            Page<PointHistoryDto> result = pointService.getHistory(1L, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 유저")
        void getHistory_fail_userNotFound() {
            given(authRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.getHistory(999L, PageRequest.of(0, 10)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}
