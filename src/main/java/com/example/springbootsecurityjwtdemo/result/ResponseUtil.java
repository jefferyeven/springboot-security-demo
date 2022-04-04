package com.example.springbootsecurityjwtdemo.result;

public class ResponseUtil {
    public static Response returnResponse(ResponseMag responseMag){
        return new Response(responseMag);
    }
    public static Response returnResponse(String rspCode){
        return new Response(rspCode);
    }
    public static Response returnResponse(String rspCode, String rspMsg){
        return new Response(rspCode,rspMsg);
    }
    public static ResponseData returnResponseData(ResponseMag msg){
        return new ResponseData(msg);
    }
    public static ResponseData returnResponseData(String rspCode, String rspMsg){
        return new ResponseData(rspCode, rspMsg);
    }
    public static ResponseData returnResponseData(String rspCode, String rspMsg, Object data){
        return new ResponseData(rspCode,rspMsg,data);
    }
    public static ResponseData returnResponseData(ResponseMag msg, Object data){
        return new ResponseData(msg,data);
    }
}
