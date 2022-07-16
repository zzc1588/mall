package com.atguigu.gulimall.ware.exception;

/**
 * @Author: 钟质昌
 * @Description: TODO 重复释放库存异常
 * @DateTime: 2022-06-27 12:11
 **/
public class RepeatReleaseException extends  RuntimeException{
    public RepeatReleaseException(Long taskId){
        super("异常=>重复释放库存,task-id："+taskId);
    }
}
