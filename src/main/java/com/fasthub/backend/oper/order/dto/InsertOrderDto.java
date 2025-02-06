package com.fasthub.backend.oper.order.dto;

import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.product.entity.Product;

import java.util.List;

public class InsertOrderDto {
    private User user;
    List<Product> orderProducts;
}
