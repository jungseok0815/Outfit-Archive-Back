package com.fasthub.backend.oper.product.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductDto {
    private Long id;
    private String productNm;
    private Long productPrice;
    private Long productAuantity;
}
