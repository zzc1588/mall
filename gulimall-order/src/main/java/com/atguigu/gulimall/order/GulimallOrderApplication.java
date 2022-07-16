package com.atguigu.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-04-28 10:55
 **/
@EnableFeignClients(basePackages = "com.atguigu.gulimall.order.feign")//开启远程调用功能
@SpringBootApplication
@MapperScan("com.atguigu.gulimall.order.dao")
@EnableDiscoveryClient
@EnableRedisHttpSession//整合redis作为session存储
public class GulimallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}