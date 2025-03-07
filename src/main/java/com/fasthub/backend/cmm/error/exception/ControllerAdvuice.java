package com.fasthub.backend.cmm.error.exception;

import com.fasthub.backend.cmm.error.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvuice {

    @ExceptionHandler(BusinessException.class)
    public String BusinessException(String message, ErrorCode errorCode){
        return  message;
    }


}
