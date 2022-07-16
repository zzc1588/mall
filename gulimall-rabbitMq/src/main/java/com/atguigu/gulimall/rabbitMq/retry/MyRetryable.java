package com.atguigu.gulimall.rabbitMq.retry;

import java.lang.annotation.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-06 12:12
 **/

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRetryable {
    int maxRetry() default 0;
}
