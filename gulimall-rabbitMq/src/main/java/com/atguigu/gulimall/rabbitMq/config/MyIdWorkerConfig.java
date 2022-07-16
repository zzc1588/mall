package com.atguigu.gulimall.rabbitMq.config;

import com.atguigu.common.utils.SnowflakeIdWorker;
import com.atguigu.gulimall.rabbitMq.constant.IdWorkerConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-05 14:29
 **/
@Configuration
public class MyIdWorkerConfig {
    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(){
        return new SnowflakeIdWorker(IdWorkerConstant.MQ_WORKER_ID.getCode(), IdWorkerConstant.MQ_DATACENTER_ID.getCode());
    }
}
