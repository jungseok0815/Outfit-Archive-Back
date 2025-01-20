package com.fasthub.backend.cmm.error;

import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<?> handlerRuntimeException(BusinessException e){
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder().status(e.getErrorCode().getStatus().value())
               .msg(e.getErrorCode().getMessage())
               .code(e.getErrorCode().getCode()).build());
    }
}
