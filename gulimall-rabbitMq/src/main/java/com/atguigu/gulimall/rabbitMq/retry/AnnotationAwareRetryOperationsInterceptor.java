package com.atguigu.gulimall.rabbitMq.retry;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author: 钟质昌
 * @Description: 测试手动实现重试机制
 * @DateTime: 2022-07-06 12:13
 **/
public class AnnotationAwareRetryOperationsInterceptor implements MethodInterceptor {

    //记录重试次数
    private int times = 0;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //获取拦截的方法中的Retryable 注解
        MyRetryable myRetryable = method.getAnnotation(MyRetryable.class);
        if (myRetryable == null) {
            return methodProxy.invokeSuper(o, objects);
        } else {
            //有Retryable注解
            int maxRetry = myRetryable.maxRetry();
            try {
                return methodProxy.invokeSuper(o, objects);
            } catch (Exception e) {
//                System.out.println("每隔两秒重复执行一次");
//                Thread.sleep(2000);
                if (times++ == maxRetry) {
                    System.out.println("已达到最大重试次数：" + maxRetry + " , 不再重试");
                } else {
                    System.out.println("调用 " + method.getName() + "方法异常，开始第 " + times + "次重试");
                    //注意这里不是 invokeSuper 方法，invokeSuper会退出当前interceptor的处理
                    methodProxy.invoke(o, objects);
                }
            }
        }
        return null;
    }


}