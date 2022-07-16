package com.atguigu.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class GulimallThirdPartyApplication {
    public static void main(String[] args) {
                SpringApplication.run(GulimallThirdPartyApplication.class, args);
    }

}
