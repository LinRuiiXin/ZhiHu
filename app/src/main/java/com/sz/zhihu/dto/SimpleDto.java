package com.sz.zhihu.dto;

public class SimpleDto {
    private boolean respCode;
    private String msg;
    private Object object;

    public SimpleDto(boolean respCode, String msg, Object object) {
        this.respCode = respCode;
        this.msg = msg;
        this.object = object;
    }

    public boolean isSuccess() {
        return respCode;
    }

    public void setRespCode(boolean respCode) {
        this.respCode = respCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "SimpleDto{" +
                "respCode=" + respCode +
                ", msg='" + msg + '\'' +
                ", object=" + object +
                '}';
    }
}