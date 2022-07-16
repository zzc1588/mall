package com.atguigu.gulimall.order.exception;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-03 15:36
 **/
public class MqReturnCallbackException extends RuntimeException{
    public MqReturnCallbackException(String msg){
        super("mq消息未抵达队列： "+msg);
    }
}
