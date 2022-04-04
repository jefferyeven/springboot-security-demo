
package com.example.springbootsecurityjwtdemo.result;

public enum ResponseMag {
	SUCCESS("200", "操作成功"),
	FAILED("600","操作失败"),
    ParamError("610", "参数错误！"),
    LoginError("601", "用户名或者密码错误！"),
    TokenError("605","token验证出错"),
    RepeatUserError("610","用户重复添加" ),
    NoRoleError("611","没有该权限名"),
    AuthError("620","未通过验证"),
    FileEmpty("640","上传文件为空"),
    LimitPictureSize("641","图片大小必须小于2M"),
    LimitPictureType("642","图片格式必须为'jpg'、'png'、'jpge'、'gif'、'bmp'")
    ;
   private ResponseMag(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private String code;
    private String msg;

	public String getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}


}

