package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-25 22:44
 **/
@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.useSingleServer().setPassword("123456");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
