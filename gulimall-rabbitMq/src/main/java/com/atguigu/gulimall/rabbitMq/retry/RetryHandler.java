package com.atguigu.gulimall.rabbitMq.retry;


/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-06 12:33
 **/
public class RetryHandler {
    public static void main(String[] args) {
        RetryServiceImpl retryServiceImpl = new RetryServiceImpl();
        RetryProxy retryProxy = new RetryProxy();
        //创建代理类
        RetryService retryService =(RetryService)retryProxy.newProxyInstance(retryServiceImpl);
        retryService.testRetry();
    }
}
