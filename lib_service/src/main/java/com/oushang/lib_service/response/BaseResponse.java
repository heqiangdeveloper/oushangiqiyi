package com.oushang.lib_service.response;

/**
 * @Author: zeelang
 * @Description: 接口响应基类
 * @Time: 2021/7/1 10:21
 * @Since: 1.0
 */
public class BaseResponse<T> {

    //响应状态码
    private int code;

    //响应信息
    private String msg;

    //响应数据
    private T data;

    public BaseResponse() {}

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
