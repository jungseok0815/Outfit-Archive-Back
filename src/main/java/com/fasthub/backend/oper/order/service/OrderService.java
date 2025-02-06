package com.fasthub.backend.oper.order.service;

import com.fasthub.backend.cmm.img.ImgHandler;
import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.mapper.ProductMapper;
import com.fasthub.backend.oper.order.dto.InsertOrderDto;
import com.fasthub.backend.oper.order.dto.UpdateOrderDto;
import com.fasthub.backend.oper.order.entity.Order;
import com.fasthub.backend.oper.order.repositoty.OrderRepository;
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

    private final OrderRepository saleRepository;
    private final ImgHandler imgHandler;
    private final ProductMapper productMapper;

    @Transactional
    public Result insert(InsertOrderDto insertSaleDto){
        Product productResult =  saleRepository.save();
        return Result.success("insert ok");
    }

    public Result list(String keyword, Pageable pageable){
        Page<Order> salePage = saleRepository.findAllByKeyword(keyword,pageable);
        return Result.success(salePage.map(productMapper::productToProductDto));
    }

    @Transactional
    public Result update(UpdateOrderDto updateSaleDto){
        saleRepository.findById(updateSaleDto.getId())
                .ifPresent(sale -> {
                    Product resultProduct =  sale.save();
                });
        return Result.success("update ok");
    }

    public Result delete(String id){
        saleRepository.deleteById(Long.valueOf(id));
        return Result.success("delete ok");
    }

}
