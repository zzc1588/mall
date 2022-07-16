package com.atguigu.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.atguigu.gulimall.ware.dao")
@EnableDiscoveryClient
@EnableFeignClients //配置包扫描
@EnableRabbit
public class GulimallWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }
}
