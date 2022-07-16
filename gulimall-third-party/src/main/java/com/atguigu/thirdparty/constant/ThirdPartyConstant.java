package com.atguigu.thirdparty.constant;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-06-02 21:11
 **/
public enum ThirdPartyConstant {
        SEND_EMAIL_ERROR(10002,"发送邮件验证码有异常");
        private int code;
        private String msg;

        ThirdPartyConstant(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
}
