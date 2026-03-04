package com.fasthub.backend.admin.revenue.service;

import com.fasthub.backend.admin.order.dto.RevenueByBrandDto;
import com.fasthub.backend.admin.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueService {

    private final OrderRepository orderRepository;

    public List<RevenueByBrandDto> getRevenue(Long brandId) {
        return orderRepository.findRevenueByBrand(brandId);
    }
}
