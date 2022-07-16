package com.atguigu.gulimall.product;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Arrays;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-04-28 10:55
 **/
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")//开启远程调用功能
@SpringBootApplication
@MapperScan("com.atguigu.gulimall.product.dao")
@EnableDiscoveryClient
@EnableRedisHttpSession//整合redis作为session存储
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}