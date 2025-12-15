package com.warehouse.warehouse_backend.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码：200成功，其他失败
    private String msg;   // 提示信息
    private T data;       // 具体数据

    // 成功静态方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败静态方法
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500; // 这里可以定义更细的错误码
        result.msg = msg;
        return result;
    }
}