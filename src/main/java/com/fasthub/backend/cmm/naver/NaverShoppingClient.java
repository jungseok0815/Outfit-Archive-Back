package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;

import java.util.List;

public interface NaverShoppingClient {
    List<NaverShoppingItem> search(String keyword, int display);
}
