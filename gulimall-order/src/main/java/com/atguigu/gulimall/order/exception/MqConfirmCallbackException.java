package com.atguigu.gulimall.order.exception;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 15:31
 **/
public class MqConfirmCallbackException extends RuntimeException{
    public MqConfirmCallbackException(String msg){
        super("Mq发送消息到Broker失败:  "+msg);
    }
}
