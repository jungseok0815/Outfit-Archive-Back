package com.fasthub.backend.admin.order.service;

import com.fasthub.backend.admin.order.dto.ResponseOrderDto;
import com.fasthub.backend.admin.order.dto.UpdateOrderStatusDto;
import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.admin.order.mapper.OrderMapper;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Page<ResponseOrderDto> list(String keyword, Pageable pageable) {
        return orderRepository.findAllByKeyword(keyword, pageable)
                .map(orderMapper::orderToResponseDto);
    }

    @Transactional
    public void updateStatus(UpdateOrderStatusDto dto) {
        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.updateStatus(dto.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        orderRepository.delete(order);
    }
}
