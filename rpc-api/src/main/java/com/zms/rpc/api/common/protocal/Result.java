package com.zms.rpc.api.common.protocal;

import java.io.Serializable;

public class Result implements Serializable {

    private int code;

    private Object result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
