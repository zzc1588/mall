package com.atguigu.common.exception;

/**
 * 12000  注册业务
 * 21xxx  库存
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-06 18:45
 **/

public enum BizCodeEnume {
    //远程服务调用问题
    FEIGN_EXCEPTION(9000,"系统繁忙，请稍后再试"),


    UNKNOW_EXCEPTION(10000,"系统异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，请稍后再试"),
    EMAIL_CODE_EXCEPTION(10003,"发送验证码频繁，请稍后再试"),
    NULL_POINTER_EXCEPTION(10004,"参数不能为空为空，请确认后再试"),
    CLASS_CAST_EXCEPTION(10005,"参数类型错误，请确认后再试"),

    //注册业务
    REGISTER_EMAIL_UNIQUE(12001,"该邮箱已注册"),
    REGISTER_PHONE_UNIQUE(12002,"该手机号已注册"),
    REGISTER_USERNAME_UNIQUE(12003,"该用户名已注册"),
    //并发异常
    CONCURRENT_ABORT_EXCEPTION(13001,"当前访问人数较多，请稍后再试!"),
    //登录业务
    LOGIN_PASSWORD_EXCEPTION(14001,"账号或密码错误!"),

    PRODUCT_UP_EXCEPTION(11000,"商品上架异常"),

    //ware业务
    NO_STOCK_EXCEPTION(21000,"商品库存不足");

    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
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
