package com.atguigu.gulimall.rabbitMq.retry;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-06 12:17
 **/
public class RetryProxy {
    public Object newProxyInstance(Object target){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new AnnotationAwareRetryOperationsInterceptor());
        return enhancer.create();
    }
}
