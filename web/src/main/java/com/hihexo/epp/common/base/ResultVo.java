package com.hihexo.epp.common.base;

import lombok.ToString;

@ToString
public class ResultVo<T> {

    private boolean result = true;
    private String message;
    private T data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
