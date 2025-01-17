package com.fasthub.backend.cmm.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // Member
    ID_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "M001","사용자의 아이디를 찾을 수 없습니다."),
    PWD_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR,"M002","사용자의 패스워드를 찾을 수 없습니다."),
    NOT_AUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "M003", "사용자의 권한이 인증되지 않음"),
    //BOARD
    BOARD_FAIL_SELECT(HttpStatus.INTERNAL_SERVER_ERROR,"B001","게시물 검색 실패"),
    BOARD_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR,"B002","게시물 등록 실패"),
    BOARD_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR,"B003","게시물 업데이트 실패"),
    BOARD_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR,"B004","게시물 삭제 실패"),

    //PRODUCT
    PRODUCT_FAIL_SELECT(HttpStatus.INTERNAL_SERVER_ERROR,"P001","상품 검색 실패"),
    PRODUCT_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR,"P002","상품 등록 실패"),
    PRODUCT_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR,"P003","상품 업데이트 실패"),
    PRODUCT_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR,"P004","상품 삭제 실패"),

    //JWT
    NOT_REFRESG_KEY(HttpStatus.NOT_FOUND, "CP003", "리프레쉬 키 해석 실패"),

    //file
    FAIR_CREATE_FILE(HttpStatus.NOT_FOUND,"F001","이미지 파일 생성 실패");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
