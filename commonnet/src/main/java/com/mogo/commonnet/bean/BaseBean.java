package com.mogo.commonnet.bean;

import java.io.Serializable;

/**
 * @author: xuanlonghua
 * @date: 2021/2/7
 * @version: 1.0.0
 * @description:
 */

public class BaseBean<T> implements Serializable {

    int code;

    String msg;
    /**
     * 通过泛型
     */
    T data;

    T result;

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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", result=" + result +
                '}';
    }
}
