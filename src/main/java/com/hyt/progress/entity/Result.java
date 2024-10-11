package com.hyt.progress.entity;


import lombok.Data;

@Data
public class Result {
    public static final int SUCCESS = 200;
    private int code;
    private  Object data;

    public Result() {
    }

    public Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public static Result success(Object obj) {
        return new Result(SUCCESS, obj);
    }
}
