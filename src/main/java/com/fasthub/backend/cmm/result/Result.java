package com.fasthub.backend.cmm.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private boolean success;
    private String message;
    private String error;

    public static Result success(String message){
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static Result fail(String error){
        Result result = new Result();
        result.setSuccess(false);
        result.setError(error);
        return  result;
    }
}
