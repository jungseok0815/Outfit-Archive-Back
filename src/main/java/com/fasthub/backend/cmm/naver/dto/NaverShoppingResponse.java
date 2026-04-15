package com.fasthub.backend.cmm.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverShoppingResponse {

    private int total;
    private int start;
    private int display;
    private List<NaverShoppingItem> items;
}
