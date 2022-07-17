package com.atguigu.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-31 18:32
 **/
@Configuration
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class) 配置文件已经配置了component  所以这里不需要再次导入了
public class MyThreadConfig {
    /**
     * 线程工厂，这里我们使用可命名的线程工厂，方便业务区分以及生产问题排查。
     */
    ThreadFactory addCartThreadFactory = new CustomizableThreadFactory("addCart-Thread-pool-");


    @Bean("addCartExecutor")
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(pool.getCoreSize(),pool.getMaxSize(),pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(500),
                addCartThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }


}
