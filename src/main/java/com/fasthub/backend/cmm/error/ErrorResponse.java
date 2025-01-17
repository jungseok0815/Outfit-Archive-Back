package com.fasthub.backend.cmm.error;

import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private int status;
    private String code;
    private String msg;

}
