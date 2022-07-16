package com.atguigu.gulimall.rabbitMq.retry;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-06 12:33
 **/

public class RetryServiceImpl implements RetryService{

    private int count = 0;

    @MyRetryable(maxRetry = 5)
    public void testRetry() {
        System.out.println("执行异常之前的代码");
        throw new RuntimeException();
    }

}