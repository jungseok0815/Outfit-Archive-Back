package com.fasthub.backend.cmm.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // Member
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "M001","사용자의 아이디를 찾을 수 없습니다."),
    PWD_NOT_FOUND(HttpStatus.UNAUTHORIZED,"M002","사용자의 패스워드가 일치하지 않습니다."),
    NOT_AUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "M003", "사용자의 권한이 인증되지 않음"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "M004", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "M005", "이미 존재하는 아이디입니다."),
    USER_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "M006", "사용자 정보 수정 실패"),
    USER_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "M007", "사용자 삭제 실패"),

    // Admin
    ADMIN_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "관리자 아이디를 찾을 수 없습니다."),
    ADMIN_PWD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "A002", "관리자 비밀번호가 일치하지 않습니다."),
    ADMIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "A003", "이미 존재하는 관리자 아이디입니다."),
    ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A004", "해당 기능에 대한 권한이 없습니다."),

    //BOARD
    BOARD_FAIL_SELECT(HttpStatus.INTERNAL_SERVER_ERROR,"B001","게시물 검색 실패"),
    BOARD_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR,"B002","게시물 등록 실패"),
    BOARD_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR,"B003","게시물 업데이트 실패"),
    BOARD_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR,"B004","게시물 삭제 실패"),

    //BRAND
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "BR001", "브랜드를 찾을 수 없습니다."),
    BRAND_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR, "BR002", "브랜드 등록 실패"),
    BRAND_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "BR003", "브랜드 업데이트 실패"),
    BRAND_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "BR004", "브랜드 삭제 실패"),

    //PRODUCT
    PRODUCT_FAIL_SELECT(HttpStatus.INTERNAL_SERVER_ERROR,"P001","상품 검색 실패"),
    PRODUCT_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR,"P002","상품 등록 실패"),
    PRODUCT_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR,"P003","상품 업데이트 실패"),
    PRODUCT_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR,"P004","상품 삭제 실패"),

    //ORDER
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "구매내역을 찾을 수 없습니다."),
    ORDER_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "O002", "구매 상태 변경 실패"),
    ORDER_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "O003", "구매내역 삭제 실패"),

    //COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "댓글을 찾을 수 없습니다."),
    COMMENT_FAIL_INSERT(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "댓글 등록 실패"),
    COMMENT_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "댓글 수정 실패"),
    COMMENT_FAIL_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "댓글 삭제 실패"),
    COMMENT_UNAUTHORIZED(HttpStatus.FORBIDDEN, "C005", "댓글 수정/삭제 권한이 없습니다."),

    //JWT
    NOT_REFRESG_KEY(HttpStatus.NOT_FOUND, "CP003", "리프레쉬 키 해석 실패"),

    //file
    FAIR_CREATE_FILE(HttpStatus.NOT_FOUND,"F001","이미지 파일 생성 실패");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
