package com.kk.pojo;

import lombok.Data;

/**
 * 后端统一返回结果
 */
@Data
public class Result {
    private Integer code; //编码：1成功，0为失败
    private String msg; //提示信息
    private Object data; //返回数据

    // 成功，无返回数据
    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.msg = "success";
        return result;
    }

    // 成功，携带返回数据
    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 1;
        result.msg = "success";
        return result;
    }

    // 失败，传入错误提示信息
    public static Result error(String msg) {
        Result result = new Result();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}