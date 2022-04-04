package com.example.springbootsecurityjwtdemo.result;

public class ResponseData extends Response {
    private Object data;

    public ResponseData(){

    }
    public ResponseData(Object data) {
        this.data = data;
    }

    public ResponseData(ResponseMag msg) {
        super(msg);
    }

    public ResponseData(String rspCode, String rspMsg) {
        super(rspCode, rspMsg);
    }

    public ResponseData(String rspCode, String rspMsg, Object data) {
        super(rspCode, rspMsg);
        this.data = data;
    }

    public ResponseData(ResponseMag msg, Object data) {
        super(msg);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

