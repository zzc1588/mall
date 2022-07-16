package com.atguigu.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-04-28 11:29
 **/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.gulimall.coupon.feign")//开启远程调用功能
@MapperScan("com.atguigu.gulimall.coupon.dao")
public class GulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}