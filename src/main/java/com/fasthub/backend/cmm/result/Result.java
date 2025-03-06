package com.fasthub.backend.cmm.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private boolean success;
    private String message;
    private Object data;
    private String error;

    public static Result success(){
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }

    public static Result success(String message){
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static Result success(Object data){
        Result result = new Result();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static Result success(String message, Object data){
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }


    public static Result fail(String error){
        Result result = new Result();
        result.setSuccess(false);
        result.setError(error);
        return  result;
    }

    public static Result fail(String error,Object data){
        Result result = new Result();
        result.setSuccess(false);
        result.setError(error);
        result.setData(data);
        return  result;
    }

}
