package com.fasthub.backend.oper.order.controller;

import com.fasthub.backend.cmm.result.Result;
import com.fasthub.backend.oper.order.dto.InsertOrderDto;
import com.fasthub.backend.oper.order.dto.UpdateOrderDto;
import com.fasthub.backend.oper.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService saleService;

    @GetMapping("/list")
    public Result list(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                       @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return saleService.list(keyword, null);
    }

    @PostMapping("/insert")
    public Result insert(InsertOrderDto insertSaleDto){
        return saleService.insert(insertSaleDto);
    }

    @PutMapping("/update")
    public Result update(UpdateOrderDto updateSaleDto){
        return saleService.update(updateSaleDto);
    }

    @DeleteMapping("/delete")
    public Result delete(String SaleNo){
        return saleService.delete(SaleNo);
    }

}
