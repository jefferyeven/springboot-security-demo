package com.example.springbootsecurityjwtdemo.result;

public class Response {
	/** 返回信息码*/
	private String rspCode;
	/** 返回信息内容*/
	private String rspMsg;

	public Response() {
		rspCode = ResponseMag.SUCCESS.getCode();
		rspMsg = ResponseMag.SUCCESS.getMsg();
	}

	public Response(ResponseMag msg){
		this.rspCode=msg.getCode();
		this.rspMsg=msg.getMsg();
	}

	public Response(String rspCode) {
		this.rspCode = rspCode;
		this.rspMsg = "";
	}

	public Response(String rspCode, String rspMsg) {
		this.rspCode = rspCode;
		this.rspMsg = rspMsg;
	}
	public String getRspCode() {
		return rspCode;
	}
	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}
	public String getRspMsg() {
		return rspMsg;
	}
	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}
}

