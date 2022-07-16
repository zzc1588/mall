package com.atguigu.gulimall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-07-07 15:13
 **/
@Configuration //配置类
@EnableSwagger2// 开启Swagger2的自动配置
public class SwaggerConfig {
    @Bean //配置docket以配置Swagger具体参数
    public Docket docket(Environment environment) {
        // 设置要显示swagger的环境
        Profiles of = Profiles.of("dev", "test","default");

        // 判断当前是否处于该环境
        // 通过 enable() 接收此参数判断是否要显示
        boolean flag = environment.acceptsProfiles(of);
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(flag) //配置是否启用Swagger，如果是false，在浏览器将无法访问
                .groupName("分组")
                .select()
                //RequestHandlerselectors,配置要扫描接口的方式
                //basePackage:指定要扫描的包
                //any():扫描全部
                //none():不扫描

                //withClassAnnotation:扫描类上的注解，多数是一个注解的反射对象
                //withMethodAnnotation:扫描方法上的注解
                .apis(RequestHandlerSelectors.basePackage("com.atguigu.gulimall.product.controller"))
                /**
                 * regex()：通过正则表达式控制
                 * ant()：通过 ant 控制
                 * 常用的是 any()，不做特殊处理。
                 */
//                .paths(PathSelectors.ant("/**")) //接口过滤方案。
                .paths(PathSelectors.any())
                .build();
    }

    //配置文档信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact("zzc", "http://xxx.xxx.com/联系人访问链接", "联系人邮箱");
        return new ApiInfo(
                "谷粒商城Product 模块 Swagger在线文档", // 标题
                "Product 模块 Swagger 文档", // 描述
                "v1.0", // 版本
                "http://gulimall.com/组织链接", // 组织链接
                contact, // 联系人信息
                "Apach 2.0 许可", // 许可
                "许可链接", // 许可连接
                new ArrayList<>()// 扩展
        );
    }

    /**
     * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题
     **/

}
